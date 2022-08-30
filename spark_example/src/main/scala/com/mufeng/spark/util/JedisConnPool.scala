package com.mufeng.spark.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object JedisConnPool {
  val config = new JedisPoolConfig
  //最大连接数
  config.setMaxTotal(60)
  //最大空闲连接数
  config.setMaxIdle(10)
  config.setTestOnBorrow(true)

  //服务器ip
  val redisAddress :String = ConfigConstants.redisAddress.toString
  // 端口号
  val redisPort:Int = ConfigConstants.redisPort.toInt
  //访问密码
  val redisAuth :String = ConfigConstants.redisAuth.toString
  //等待可用连接的最大时间
  val redisTimeout:Int = ConfigConstants.redisTimeout.toInt

  val pool = new JedisPool(config,redisAddress,redisPort,redisTimeout,redisAuth)

  def getConnection():Jedis = {
    pool.getResource
  }
}
