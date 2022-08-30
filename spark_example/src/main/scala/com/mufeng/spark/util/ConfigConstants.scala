package com.mufeng.spark.util

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Seconds

object ConfigConstants {

  /**
   *
   CREATE TABLE `topic_par_group_offset` (
    `topic` varchar(255) NOT NULL,
    `partition` int(11) NOT NULL,
    `groupid` varchar(255) NOT NULL,
    `offset` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`topic`,`partition`,`groupid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

   */
  // Kafka配置
  val kafkaBrokers = "kms-2:9092,kms-3:9092,kms-4:9092"
  val groupId = "group_test"
  val kafkaTopics = "test"
  val batchInterval = Seconds(5)
  val streamingStorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2
  val kafkaKeySer = "org.apache.kafka.common.serialization.StringSerializer"
  val kafkaValueSer = "org.apache.kafka.common.serialization.StringSerializer"
  val sparkSerializer = "org.apache.spark.serializer.KryoSerializer"
  val batchSize = 16384
  val lingerMs = 1
  val bufferMemory = 33554432
  // MySQL配置
  val user = "root"
  val password = "123qwe"
  val url = "jdbc:mysql://localhost:3306/kafka_offset"
  val driver = "com.mysql.jdbc.Driver"
  // 检查点配置
  val checkpointDir = "file:///e:/checkpoint"
  val checkpointInterval = Seconds(10)
  // Redis配置
  val redisAddress = "192.168.10.203"
  val redisPort = 6379
  val redisAuth = "123qwe"
  val redisTimeout = 3000

}
