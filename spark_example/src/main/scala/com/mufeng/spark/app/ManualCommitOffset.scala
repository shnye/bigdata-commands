package com.mufeng.spark.app

import com.mufeng.spark.util.{ConfigConstants, OffsetReadAndSave}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies, OffsetRange}

object ManualCommitOffset {
  def main(args: Array[String]): Unit = {

    val brokers = ConfigConstants.kafkaBrokers
    val groupId = ConfigConstants.groupId
    val topics = ConfigConstants.kafkaTopics
    val batchInterval = ConfigConstants.batchInterval

    val conf = new SparkConf()
      .setAppName(ManualCommitOffset.getClass.getSimpleName)
      .setMaster("local[1]")
      //替换默认的序列化机制
      .set("spark.serializer",ConfigConstants.sparkSerializer)
      // 注册需要使用 kryo 序列化的自定义类
      //.registerKryoClasses(Array(classOf[Searcher]))

    val ssc = new StreamingContext(conf, batchInterval)
    // 必须开启checkpoint,否则会报错
    ssc.checkpoint(ConfigConstants.checkpointDir)

    ssc.sparkContext.setLogLevel("OFF")
    //使用broker和topic创建direct kafka stream
    val topicSet = topics.split(" ").toSet

    // kafka连接参数
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean),
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )


    // 从MySQL中读取该主题对应的消费者组的分区偏移量
    val offsetMap = OffsetReadAndSave.getOffsetMap(groupId, topics)
    var inputDStream: InputDStream[ConsumerRecord[String, String]] = null

    //如果MySQL中已经存在了偏移量,则应该从该偏移量处开始消费
    if (offsetMap.size > 0) {
      println("存在偏移量，从该偏移量处进行消费！！")

      inputDStream = KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topicSet, kafkaParams, offsetMap))

    } else {
      //如果MySQL中没有存在了偏移量，从最早开始消费
      inputDStream = KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topicSet, kafkaParams))

    }
    // checkpoint时间间隔，必须是batchInterval的整数倍
    inputDStream.checkpoint(ConfigConstants.checkpointInterval)

    // 保存batch的offset
    var offsetRanges = Array[OffsetRange]()
    // 获取当前DS的消息偏移量
    val transformDS = inputDStream.transform { rdd =>
      // 获取offset
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }



    /**
     * 状态更新函数
     * @param newValues:新的value值
     * @param stateValue：状态值
     * @return
     */
    def updateFunc(newValues: Seq[Int], stateValue: Option[Int]): Option[Int] = {
      var oldvalue = stateValue.getOrElse(0) // 获取状态值
      // 遍历当前数据，并更新状态
      for (newValue <- newValues) {
        oldvalue += newValue
      }
      // 返回最新的状态
      Option(oldvalue)
    }

    // todo 业务逻辑处理
    // 该示例统计消息key的个数，用于查看是否是从已经提交的偏移量消费数据
    transformDS.map(meg => ("spark", meg.value().toInt)).updateStateByKey(updateFunc).print()

    // 打印偏移量和数据信息，观察输出的结果
    transformDS.foreachRDD { (rdd, time) =>
      // 遍历打印该RDD数据
      rdd.foreach { record =>
        println(s"key=${record.key()},value=${record.value()},partition=${record.partition()},offset=${record.offset()}")
      }
      // 打印消费偏移量信息
      for (o <- offsetRanges) {
        println(s"topic=${o.topic},partition=${o.partition},fromOffset=${o.fromOffset},untilOffset=${o.untilOffset},time=${time}")

      }

      //将偏移量保存到到MySQL中
      OffsetReadAndSave.saveOffsetRanges(groupId, offsetRanges)
    }
    ssc.start()
    ssc.awaitTermination()
  }

}
