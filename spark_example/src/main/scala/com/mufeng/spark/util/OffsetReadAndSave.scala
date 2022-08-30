package com.mufeng.spark.util

import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import redis.clients.jedis.Jedis

import java.sql.ResultSet
import scala.collection.{JavaConversions, mutable}

object OffsetReadAndSave {
  /**
   * 从MySQL中获取偏移量
   *
   * @param groupid
   * @param topic
   * @return
   */

  def getOffsetMap(groupid: String, topic: String): mutable.Map[TopicPartition, Long] = {

    val conn = JDBCConnPool.getConnection()
    val selectSql = "select * from topic_par_group_offset where groupid = ? and topic = ?"
    val ppst = conn.prepareStatement(selectSql)
    ppst.setString(1, groupid)
    ppst.setString(2, topic)

    val result: ResultSet = ppst.executeQuery()

    // 主题分区偏移量
    val topicPartitionOffset = mutable.Map[TopicPartition, Long]()

    while (result.next()) {

      val topicPartition: TopicPartition = new TopicPartition(result.getString("topic"), result.getInt("partition"))

      topicPartitionOffset += (topicPartition -> result.getLong("offset"))
    }

    JDBCConnPool.closeConnection(ppst, conn)
    topicPartitionOffset
  }

  /**
   * 从Redis中获取偏移量
   *
   * @param groupid
   * @param topic
   * @return
   */
  def getOffsetFromRedis(groupid: String, topic: String): Map[TopicPartition, Long] = {
    val jedis: Jedis = JedisConnPool.getConnection()
    var offsets = mutable.Map[TopicPartition, Long]()

    val key = s"${topic}_${groupid}"
    val fields : java.util.Map[String, String] = jedis.hgetAll(key)
    for (partition <- JavaConversions.mapAsScalaMap(fields)) {

      offsets.put(new TopicPartition(topic, partition._1.toInt), partition._2.toLong)
    }

    offsets.toMap

  }
  /**
   * 将偏移量写入MySQL
   *
   * @param groupid     消费者组ID
   * @param offsetRange 消息偏移量范围
   */

  def saveOffsetRanges(groupid: String, offsetRange: Array[OffsetRange]) = {

    val conn = JDBCConnPool.getConnection()
    val insertSql = "replace into topic_par_group_offset(`topic`, `partition`, `groupid`, `offset`) values(?,?,?,?)"
    val ppst = conn.prepareStatement(insertSql)

    for (offset <- offsetRange) {

      ppst.setString(1, offset.topic)
      ppst.setInt(2, offset.partition)
      ppst.setString(3, groupid)
      ppst.setLong(4, offset.untilOffset)
      ppst.executeUpdate()
    }
    JDBCConnPool.closeConnection(ppst, conn)

  }
  /**
   * 将偏移量保存到Redis中
   * @param groupid
   * @param offsetRange
   */
  def saveOffsetToRedis(groupid: String, offsetRange: Array[OffsetRange]) = {
    val jedis :Jedis = JedisConnPool.getConnection()
    for(offsetRange<-offsetRange){
      val topic=offsetRange.topic
      val partition=offsetRange.partition
      val offset=offsetRange.untilOffset
      // key为topic_groupid,field为partition，value为offset
      jedis.hset(s"${topic}_${groupid}",partition.toString,offset.toString)
    }
  }

}
