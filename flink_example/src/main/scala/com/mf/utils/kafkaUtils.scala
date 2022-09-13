package com.mf.utils

import java.util.Properties
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer}
import com.mf.config

object kafkaUtils {
  def GetKafkaStream(env: StreamExecutionEnvironment,topic:String): DataStream[String] ={

    val properties = new Properties()
    properties.setProperty("bootstrap.servers",config.KAFKA_BOOTSTRAP_SERVERS)
    properties.setProperty("group.id", config.KAFKA_GROUP_ID)
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("enable,auto.commit","false")
    //properties.setProperty("auto.offset.reset", "earliest")

    val KafkaStream: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties))
    KafkaStream
  }
}
