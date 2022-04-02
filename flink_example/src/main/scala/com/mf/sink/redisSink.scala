package com.mf.sink

import com.mf.bean.result
import com.mf.config
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object redisSink {

  val conf: FlinkJedisPoolConfig = new FlinkJedisPoolConfig.Builder()
    .setHost(config.REDIS_HOST)
    .setPort(config.REDIS_PORT)
    .setPassword(config.REDIS_PASSWORD)
    .build()

  //定义保存数据到redis的命令 host table_name key value
  val myMapper: RedisMapper[result] = new RedisMapper[result]{
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET,"RealtimeStatisticsSource")
    }
    override def getKeyFromData(data: result): String = {
      // TODO: 设置插入key格式
      data.key
    }
    override def getValueFromData(data: result): String = {
      // TODO: 设置插入值格式
      data.value
    }
  }


}
