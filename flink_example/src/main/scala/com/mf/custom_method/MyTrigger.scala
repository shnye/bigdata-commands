package com.mf.custom_method

import com.mf.bean.result
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

class MyTrigger extends Trigger[result,TimeWindow] {

  //TODO onElement()添加到每个窗口的元素都会调用此方法。
  //  onEventTime()当注册的事件时间计时器触发时，将调用此方法。
  //  onProcessingTime()当注册的处理时间计时器触发时，将调用此方法。
  //  onMerge()与有状态触发器相关，并在两个触发器对应的窗口合并时合并它们的状态，例如在使用会话窗口时。
  //  clear()执行删除相应窗口时所需的任何操作。(一般是删除定义的状态、定时器等)

  // TODO TriggerResult包含以下内容
  //  CONTINUE：表示啥都不做。
  //  FIRE：表示触发计算，同时保留窗口中的数据
  //  PURGE：简单地删除窗口的内容，并保留关于窗口和任何触发器状态的任何潜在元信息。
  //  FIRE_AND_PURGE：触发计算，然后清除窗口中的元素。（默认情况下，预先实现的触发器只触发而不清除窗口状态。）



  //TODO 元素触发计算
  override def onElement(element: result, timestamp: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }

  //TODO 执行时间定时器触发
  override def onProcessingTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }

  //TODO 事件时间定时器触发
  override def onEventTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE
  }

  override def clear(window: TimeWindow, ctx: Trigger.TriggerContext): Unit = {
    println("这里设置结束时候的运行逻辑")
  }

  override def onMerge(window: TimeWindow, ctx: Trigger.OnMergeContext): Unit = {
    println("这里设置合并逻辑")
  }
}
