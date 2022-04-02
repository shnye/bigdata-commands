package com.mf.custom_method

import java.lang

import com.mf.bean.result
import org.apache.flink.streaming.api.windowing.evictors.Evictor
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.runtime.operators.windowing.TimestampedValue


//todo 需求背景 每隔2个单词统计最近3个单词 如果是普通的window+trigger 窗口结束会清空状态。
// 则需要使用trigger(file) + Evictor 使达到预定条件后再清除状态
// 或者是去除窗口中不符合规则的元素

class MyEvictor extends Evictor[result, TimeWindow]{

  //TODO 这里设置窗口前驱逐的规则
  /**
   * 在应用窗口前驱除不需要的元素
   *
   * @param elements   当前在窗口中的元素
   * @param size       窗口中元素数量
   * @param window 当前窗口
   * @param evictorContext        Evictor上下文
   */
  override def evictBefore(elements: lang.Iterable[TimestampedValue[result]], size: Int, window: TimeWindow, evictorContext: Evictor.EvictorContext): Unit = {

  }


  //TODO 这里设置窗口后驱逐的规则
  override def evictAfter(elements: lang.Iterable[TimestampedValue[result]], size: Int, window: TimeWindow, evictorContext: Evictor.EvictorContext): Unit = {

  }
}
