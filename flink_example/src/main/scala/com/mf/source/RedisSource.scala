package com.mf.source

import com.mf.bean.result
import com.mf.utils.RedisUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.slf4j.LoggerFactory
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

class RedisSource extends RichSourceFunction[result] {

  val Logger = LoggerFactory.getLogger("RedisSource")
  var redis_con:Jedis  = null


  override def open(parameters: Configuration): Unit = {
    //todo 初始化获取连接
    redis_con = RedisUtils.getJedisClient
  }

  override def run(ctx: SourceFunction.SourceContext[result]): Unit = {
    //todo 取数逻辑
    val elem_keys = redis_con.hkeys("test")
    val keys_iter = elem_keys.iterator()
    while(keys_iter.hasNext){
      val key = keys_iter.next()
      val value = redis_con.hget("test",key)
      val time = 10000L
      ctx.collect(result(key,value,time))
    }
  }

  override def cancel(): Unit = {
    redis_con.close()
  }
}
