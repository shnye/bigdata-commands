package com.mf.app

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema
import io.debezium.data.Envelope
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.kafka.connect.source.SourceRecord
import org.apache.flink.table.api.AnyWithOperations
import org.apache.kafka.common.serialization.Serdes.String
import com.alibaba.fastjson.JSONObject
import org.apache.flink.util.Collector
import org.apache.kafka.connect.data.Struct
import org.apache.flink.streaming.api.scala._

import java.util.Properties

object MySQLCDC {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //TODO 2.开启检查点   Flink-CDC将读取binlog的位置信息以状态的方式保存在CK,如果想要做到断点续传,
    // 需要从Checkpoint或者Savepoint启动程序
    //2.1 开启Checkpoint,每隔5秒钟做一次CK  ,并指定CK的一致性语义
   // env.enableCheckpointing(5000L, CheckpointingMode.EXACTLY_ONCE);
    //2.2 设置超时时间为1分钟
    //env.getCheckpointConfig().setCheckpointTimeout(60000);
    //2.3 指定从CK自动重启策略
    //env.setRestartStrategy(RestartStrategies.fixedDelayRestart(2,2000L));
    //2.4 设置任务关闭的时候保留最后一次CK数据
    //env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    //2.5 设置状态后端
    //env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/flinkCDC"));
    //2.6 设置访问HDFS的用户名
    //System.setProperty("HADOOP_USER_NAME", "root");

    //TODO 3.创建Flink-MySQL-CDC的Source
    val props = new Properties();
    props.setProperty("scan.startup.mode","initial");
    val sourceFunction:SourceFunction[String] = MySQLSource.builder()
      .hostname("115.29.209.31")
      .port(3306)
      .username("root")
      .password("123000")
      .databaseList("test")
      ///可选配置项,如果不指定该参数,则会读取上一个配置中指定的数据库下的所有表的数据
      //注意：指定的时候需要使用"db.table"的方式
      .tableList("test.student")
      .debeziumProperties(props)
      .deserializer(new StringDebeziumDeserializationSchema())
      //.deserializer(new MySchema())
      .build();

    //TODO 4.使用CDC Source从MySQL读取数据
    val mysqlDS:DataStreamSource[String] = env.addSource(sourceFunction)

    val value = mysqlDS.broadcast()
    //TODO 5.打印输出
    mysqlDS.print();

    //TODO 6.执行任务
    env.execute();
  }


  /*
  自定义返回值的反序列化器
   */
  class MySchema extends DebeziumDeserializationSchema[String] {
    override def deserialize(sourceRecord: SourceRecord, collector: Collector[String]): Unit = {
      val topic = sourceRecord.topic
      val topicArr = topic.split("\\.")
      val dbName = topicArr(1)
      val tableName = topicArr(2)
      //获取操作类型
      val operation = Envelope.operationFor(sourceRecord)
      //获取变更的数据  value=Struct{after=Struct{id=3,name=ww11,age=55666}
      val valueStruct = sourceRecord.value.asInstanceOf[Struct]
      val afterStruct = valueStruct.getStruct("after")
      //将变更数据封装为一个json对象
      val dataJsonObj = new JSONObject()
      if (afterStruct != null) {
        import scala.collection.JavaConversions._
        for (field <- afterStruct.schema.fields) {
          val o = afterStruct.get(field)
          dataJsonObj.put(field.name, o)
        }
      }
      //创建JSON对象用于封装最终返回值数据信息
      val result = new JSONObject()
      result.put("operation", operation.toString.toLowerCase)
      result.put("data", dataJsonObj)
      result.put("database", dbName)
      result.put("table", tableName)
      //发送数据至下游
      collector.collect(result.toJSONString)
    }

    override def getProducedType: TypeInformation[String] = TypeInformation.of(classOf[String])

  }

}



