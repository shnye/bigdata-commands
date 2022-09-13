package com.mufeng.spark.app

import org.apache.spark.{SparkConf, SparkContext}

object WorldCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WorldCount").setMaster("local")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("D:\\data\\spark\\wordcount.txt")
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map((_,1))
    val wordCounts = pairs.reduceByKey(_+_)
    wordCounts.foreach(wordCount => println(wordCount._1 + " appears " + wordCount._2 + " times."))
    sc.stop()
  }
}
