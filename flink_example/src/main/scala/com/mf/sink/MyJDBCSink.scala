package com.mf.sink

import java.sql.{Connection, DriverManager, PreparedStatement}

import com.mf.bean.result
import com.mf.config
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

//自定义jdbc sink 将整点的的数据输出到mysql结果表中
class MyJDBCSink() extends RichSinkFunction[result]{
  //首先定义sql连接，以及预编译语句
  var conn:Connection = _
  var insertStmt:PreparedStatement =_
  //var updateStmt:PreparedStatement =_

  //在open生命周期中创建连接以及预编译语句
  override def open(parameters: Configuration): Unit = {
    conn = DriverManager.getConnection(config.MYSQL_URL,config.MYSQL_USER,config.MYSQL_PASSWORD)
    insertStmt = conn.prepareStatement(
      "insert into example (key,value) values ( ?, ?)")
    //updateStmt = conn.prepareStatement("update temp set temperature = ? where sensor = ?")
  }
  override def invoke(value: result, context: SinkFunction.Context[_]): Unit = {

    //TODO 插入数据
    val pv = value.value
    for (elem <- pv) {
      insertStmt.setString(1,value.key)
      insertStmt.execute()
    }
  }

  override def close(): Unit = {
    insertStmt.close()
    conn.close()
  }
}

