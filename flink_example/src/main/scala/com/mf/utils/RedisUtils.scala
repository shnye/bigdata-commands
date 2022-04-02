package com.mf.utils

import com.mf.config

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}


// todo redis连接池
object RedisUtils {

  var jedisPool:JedisPool = null

  def getJedisClient:Jedis = {
    if(jedisPool == null){
      /*todo 通过配置文件的方式加载
      val config = PropertiesUtil.load("config.properties")
      val host = config.getProperty("redis.host")
      val port = config.getProperty("redis.port")
      */

      //TODO 通过变量形式读取
      val host = config.REDIS_HOST
      val port = config.REDIS_PORT
      val pass = config.REDIS_PASSWORD

      val jedisPoolConfig = new JedisPoolConfig()
      jedisPoolConfig.setMaxTotal(10)  //最大连接数
      jedisPoolConfig.setMaxIdle(4)   //最大空闲
      jedisPoolConfig.setMinIdle(4)     //最小空闲
      jedisPoolConfig.setBlockWhenExhausted(true)  //忙碌时是否等待
      jedisPoolConfig.setMaxWaitMillis(5000)//忙碌时等待时长 毫秒
      jedisPoolConfig.setTestOnBorrow(true) //每次获得连接的进行测试

      jedisPool = new JedisPool(jedisPoolConfig,host,port,10000,pass)
    }
    jedisPool.getResource
  }

  def closeJedisClient = {
    print("--------关闭redis连接--------")
    jedisPool.close()
  }
}
