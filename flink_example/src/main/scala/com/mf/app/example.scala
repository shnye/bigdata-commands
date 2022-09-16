package com.mf.app

import com.mf.bean.result
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.contrib.streaming.state.{EmbeddedRocksDBStateBackend, RocksDBStateBackend}
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala._
import com.mf.config
import com.mf.sink.{MyJDBCSink, redisSink}
import com.mf.utils.{MyPeriodicAssigner, kafkaUtils}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.runtime.state.storage.{FileSystemCheckpointStorage, JobManagerCheckpointStorage}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{ContinuousEventTimeTrigger, ContinuousProcessingTimeTrigger}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.runtime.operators.GenericWriteAheadSink

import java.util.Properties

object example {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //1.启用checkpoints检查点，指定触发检查点的（插入barrier）间隔时间
    /*
    flink1.13 状态后端新老版本参考
    格式 ：env.setStateBackend = env.setStateBackend + env.getCheckpointConfig.setCheckpointStorage
    MemoryStateBackend = 	HashMapStateBackend + JobManagerCheckpointStorage
    FsStateBackend = 	HashMapStateBackend + FileSystemCheckpointStorage
    RocksDBStateBackend	= EmbeddedRocksDBStateBackend + FileSystemCheckpointStorage
     */
    env.setStateBackend( new EmbeddedRocksDBStateBackend(true))
    //env.setStateBackend( new RocksDBStateBackend(config.CHECK_POINT_URL, true) )   1.13以前的版本

    /*
    //设置检查点存储方法一：存储检查点到 JobManager 堆内存 1.13以后才加入
    env.getCheckpointConfig
      .setCheckpointStorage(new JobManagerCheckpointStorage())
     */
    // 设置检查点存储方法二：配置存储检查点到文件系统 1.13以后才加入
    env.getCheckpointConfig
      .setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://namenode:40010/flink/checkpoints"))


    //TODO 设置时间为事件时间，下面还需定义watermark 若不设置则默认为processingTime
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //TODO 设置waterMark产生的周期为1s 系统默认为200毫秒，一般使用系统默认即可
    env.getConfig.setAutoWatermarkInterval(1000)

    //TODO 设置checkpoint时间间隔，如果数据量大建议设置长一些 一般为5分钟
    env.enableCheckpointing(1000L)

    //2.其他配置
    env.getCheckpointConfig
      .setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE) //默认EXACTLY_ONCE
    env.getCheckpointConfig
      .setCheckpointTimeout(10000L) //超时时间，过时间则弃用
    env.getCheckpointConfig
      .setMaxConcurrentCheckpoints(1)  //同一时间进行的checkPoints个数

    // 启用不对齐的检查点保存方式:不再执行检查点的分界线对齐操作，启用之后可以大大减少产生背压时的检查点保存时间
    //需要setCheckpointingMode为EXACTLY_ONCE；setMaxConcurrentCheckpoints为1
    env.getCheckpointConfig
      .enableUnalignedCheckpoints()




    env.getCheckpointConfig
      .setMinPauseBetweenCheckpoints(500L) //两次做checkPoint之间，至少要留下多少时间处理数据 （完成checkPoint的时间）
    env.getCheckpointConfig
      .setPreferCheckpointForRecovery(false) //恢复是checkPoint恢复(true)还是savePoint(false)恢复 1.7之前没有
    env.getCheckpointConfig
      .setTolerableCheckpointFailureNumber(3) //允许checkPoint失败几次 如果设置0 失败则视为task挂了


    //重启策略的配置,默认不设置
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,10000L) ) //重启次数和时间

    val inputStream = kafkaUtils.GetKafkaStream(env, "topic_name")

    val filterStream = inputStream
      .map(line => {
      // TODO: 数据处理逻辑编写
      result("","",112211)
    }).filter(line =>{
      // TODO: 过滤逻辑编写
      line.key == ""
    })
    //TODO 给一个没有乱序，时间为升序的流设置一个EventTime
    .assignAscendingTimestamps(_.time)



    //TODO 1、当流中存在时间乱序问题，引入watermark，并设置延迟时间
    // * 1、BoundedOutOfOrdernessTimestampExtractor中的泛型为流中数据的类型
    // * 2、传入的参数为 watermark 的最大延迟时间（即允许数据迟到的时间）
    // * 3、重写的extractTimestamp方法返回的是设置数据中EventTime的字段，单位为毫秒，需要将时间转换成Long（最近时间为13位的长整形）才能返回
    // * 4、当我们能大约估计到流中的最大乱序时，建议使用此中方式，比较方便
    val watermarkStream: DataStream[result] = filterStream.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[result](Time.seconds(1)) {
      override def extractTimestamp(element: result): Long = {
        element.time * 1000
      }
    })

    //todo 2、使用 TimestampAssigner 引入 Watermark
    // Assigner with periodic watermarks（周期性引入watermark）
    // * 1、系统会周期性的将watermark插入到流中，默认周期是200毫秒，可以使用ExecutionConfig.setAutoWatermarkInterval()方法进行设置，单位为毫秒
    // * 2、产生watermark的逻辑：每隔5秒钟，Flink会调用AssignerWithPeriodicWatermarks的getCurrentWatermark()方法，如果大于流中最大watermark就插入，小于就不插入
    // * 3、如下，可以自定义一个周期性的时间戳抽取（需要实现 AssignerWithPeriodicWatermarks 接口）
    env.getConfig.setAutoWatermarkInterval(5000)
    val periodicWatermarkStream: DataStream[result] = filterStream.assignTimestampsAndWatermarks(new MyPeriodicAssigner(10))



    //todo 设置标签申明诗句结构，与 sideOutputLateData 配合使用
    val outputTag = new OutputTag[result]("late_data")

    val outputStream = filterStream
      .keyBy(_.key)
      //todo 解决时区问题
      .window(TumblingProcessingTimeWindows.of(Time.days(1), Time.hours(-8)))
      //todo 设置触发器 根据实际需求使用ContinuousEventTimeTrigger/ContinuousProcessingTimeTrigger/等/自定义
      .trigger(ContinuousProcessingTimeTrigger.of[TimeWindow](Time.seconds(5)))
      //TODO 设置允许时间迟到时间（一般不用，会在watermark定义时就做好等待时间设置），不设置则为弃用迟到数据
      .allowedLateness(Time.seconds(2))
      //TODO 一般会把迟到的数据进行收集,根据标签进行收集
      .sideOutputLateData(outputTag)
      // TODO: 编写聚合逻辑代码，可以自定义processFunction进行计算
      .sum(1)

    


    //todo 通过标签获取侧输出流
    val hourOutputStream = outputStream.getSideOutput(outputTag)
    hourOutputStream.addSink(new MyJDBCSink)

    //todo 输出到redis
    outputStream.addSink(new RedisSink[result](redisSink.conf,redisSink.myMapper))

    //TODO 输出到kafka
    // 注意 假如输入输出都是kafka flink 会自动做好数据一致性的处理，不需要额外操作
    //outputStream.addSink(new FlinkKafkaProducer011[String](config.KAFKA_BOOTSTRAP_SERVERS,"topic",new SimpleStringSchema()))




  }




}
