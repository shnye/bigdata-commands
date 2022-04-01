package com.mf.utils

import com.mf.bean.result
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark


/**
 * 自定义一个周期生成watermark的类
 * @param bound watermark的延时时间（毫秒）
 */
class MyPeriodicAssigner(bound: Long) extends AssignerWithPeriodicWatermarks[result] {
  // 当前为止的最大时间戳（毫秒）
  var maxTs: Long = Long.MinValue

  /**
   * 获取当前的watermark（默认200毫秒获取一次，可以通过 env.getConfig.setAutoWatermarkInterval(5000) 来设置）
   * @return 当前watermark，当前最大时间戳 - 延时时间
   */
  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTs - bound)
  }

  /**
   * 指定eventTime对应的字段（流中每条数据都会调用一次此方法）
   * @param element 流中的每条数据
   * @param previousElementTimestamp 无
   * @return 当前流的eventTime（单位：毫秒）
   */
  override def extractTimestamp(element: result, previousElementTimestamp: Long): Long = {
    // 每条数据都获取其中的时间戳，跟最大时间戳取大，并重新赋值给最大时间戳
    maxTs = maxTs.max(element.time * 1000)
    element.time * 1000
  }
}
