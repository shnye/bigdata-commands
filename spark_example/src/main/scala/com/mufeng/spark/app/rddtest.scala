package com.mufeng.spark.app

import com.mufeng.spark.util.ConfigConstants
import org.apache.spark._
import org.apache.spark.sql.SparkSession

object rddtest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("sparkSql")
      .master("local[2]")
      .getOrCreate()
    val rdd1 = spark.sparkContext.makeRDD(List(("a", 1), ("b", 2), ("c", 3),("a",2)))
      //.map(x => user(x._1, x._2))
    import spark.implicits._
    val user1 = rdd1.toDF("id","Count").as[user]
    user1.show()
  }
}

case class user(id:String,Count:Int)
