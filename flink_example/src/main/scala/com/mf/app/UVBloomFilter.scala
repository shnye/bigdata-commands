package com.mf.app


import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import org.apache.flink.api.common.functions.{AggregateFunction, MapFunction}
import org.apache.flink.shaded.guava18.com.google.common.hash.{BloomFilter, Funnels}
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

import org.apache.flink.streaming.api.windowing.windows.TimeWindow

import java.lang


object UVBloomFilter {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    env.readTextFile("UserBehavior.csv")
      .map(new MapFunction[String, UserBehavior]() {
        override def map(value: String): UserBehavior = {
          val arr = value.split(",")
          new UserBehavior(arr(0), arr(1), arr(2), arr(3), arr(4).toLong * 1000L)
      }
     }).filter((r) => "pv".equals(r.behaviorType))
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[UserBehavior](Time.seconds(0)) {
        override def extractTimestamp(element: UserBehavior): Long = {
          element.timestamp * 1000
        }
      })
      .map((r) => ("key", r.userId))
      .keyBy(_._1)
      .window(TumblingEventTimeWindows.of(Time.hours(1)))
      .aggregate(new Agg(), new WindowResult())
      .print()

    env.execute();

  }



  class Agg() extends AggregateFunction[Tuple2[String, String],Tuple2[BloomFilter[Long],Long],Long] {
    override def createAccumulator(): (BloomFilter[Long], Long) = {
      // 假设独立用户数量是一百万，误判率是 0.01
      return Tuple2(BloomFilter.create(Funnels.longFunnel(), 1000000, 0.01),0L)
    }

    override def add(in: (String, String), acc: (BloomFilter[Long], Long)): (BloomFilter[Long], Long) = {
      if (!acc._1.mightContain(in._2.toLong)) {
        // 如果 userID 没来过，那么执行 put 操作
        acc._1.put(in._1.toLong)
        val bloom:BloomFilter[Long] = acc._1
        val count:Long= acc._2 + 1L // UV 数量加一
        return (bloom,count)
      }
      return acc;
    }

    override def getResult(acc: (BloomFilter[Long], Long)): Long = {
      return acc._2;
    }

    override def merge(acc: (BloomFilter[Long], Long), acc1: (BloomFilter[Long], Long)): (BloomFilter[Long], Long) = {
      return null;
    }
  }

  class WindowResult extends ProcessWindowFunction[Long, String,String, TimeWindow] {
    override def process(key: String, context: Context, elements: Iterable[Long], out: Collector[String]): Unit = {
      out.collect("窗口结束时间：" + context.window.getEnd + "，UV数量：" + elements.iterator.next())
    }
  }


}

case class UserBehavior (userId:String,
                         itemId:String,
                         categoryId:String,
                         behaviorType:String,
                         timestamp:Long)