A、由于是为flume docker服务而写的，所以尽量只支持网络相关的source，sinks及channels。所以类似exec、spooldir,TAILDIR等暂不支持。flume容器中关键文件：
  flume.conf :flume的配置文件，由flumeParse.py自动生成
  flume.log  :flume的运行日志
  start.sh   :服务自动启动入口，调用flumeParser解析生成flume.conf后启动flume服务
  default.json : 默认的组件配置，由flumeParser解析提取
  环境变量CONFIG : 用户创建flume容器或flume服务时用于配置flume功能的json串。是fulum配置最关键的部分
  flumeParser.py :将default.json和环境变量CONFIG合并提取并生成flume.conf的解析器。可直接由python3执行
  
B、docker集群已有的kafka、hdfs，elsticsearch将与flume集成。各自服务的名字和端口会默认配置到default.json中。并在后续版本中持续迭代集成
C、默认的系统提供default.json，给文件将由flumeParser程序与自定义配置的CONFIG环境变量合并，并检查配置的完备性。然后生成conf/flume-conf.properties。
D、conf/flume-conf.properties 将被软链接到/flume.conf
   logs/flume.log 将被软链接到 /flume.log
   以方便跟踪查看
E、系统提供的对应的组件名：
   sources有7项："rNetcat","rNetcatudp","rHTTP","rKafka","rAvro","rMultiportsSyslogtcp","rTaildir"
   sinks有7项："kLogger","kAvro","kHDFS","kFileRoll","kES","kHTTP","kKafka"
   channels有3项："cMemory","cFile","cKafka"
   interceptors有4项："iTimestamp","iStatic","iSearchReplace","iRegexExtractor"
   selector有2项："sReplicating"，"sMultiplexing"
   processor有2项："pFailover"，"pLoadBlance"
F、默认的功能选项中rNetcat，rNetcatudp，rHTTP，kHTTP默认用10086端口
   rAvro，kAvro默认用10010端口，rMultiportsSyslogtcp默认用10011，10012，10013端口。这些都可以用port/ports修改配置
G、配置镜像或服务时要加入CONFIG的环境变量，如果不加则默认为采用source：rNetcat，sinks：rLogger，channels：rMemory。具体参见default.json。配置CONFIG时要注意在镜像启动时要用单引号将json串括起来，而在配置服务yml时，则不能用单引号包裹json
H、配置CONFIG时遵循一下组件模式：
 {"sources":...,"sinks":....,"channels":...,"interceptors":....,"selector":....,"processor":....}
 其中interceptors，selector，processor为可选件。sources，sinks，channels为必选件，不选的将采用default默认的(见G项)
I、配置CONFIG 的json脚本保持最大的灵活度，支持一下几种写法：
  1、字符串或字符串数组，如：
    {"sources":"rAvro"} 或 {"channels":["cMemory","cFile"]}
  2、字符串组件的json配置，这种最常用。如:
    {"sinks":[{"kAvro":{"hostname":"flume2","port":12345}},{"kAvro":{"hostname":"flume3","port":12345}}]
    这种方式，与default.json对应组件合并，并替换默认的属性值，以及增加必要的属性值
  3、不管系统的字符串组件，直接按照flume原生文档配置。这种方式比较繁琐，单灵活度最高如：
   {"sources":{"type":"org.apache.flume.source.kafka.KafkaSource","batchSize":"5000","batchDurationMillis":"2000","kafka.topics":"flumeTopic","kafka.bootstrap.servers":"kafka1:9092,kafka2:9092,kafka3:9092","kafka.consumer.group.id":"flume"}}
  4、可以混用，如：
   {"sinks":["kLogger",{"kAvro":{"port":10020}},"kKafka",{"type":"avro","bind":"0.0.0.0","port":"10021"}]}
J、sinks与channels的数量应当一致，这样系统将自动按顺序自动创建对应关系。selector.mapping 不支持mapping到多个channel，遵循的规则是按照channel的顺序对应mapping
K、sources中可以由原来默认rNetcat改为默认rTaildir,默认的filegroups.f1指向~/.flume/file_log/.*log.*。同时由于这个source是指向本地，所以flume容器/服务启动时应当设定一个本地映射，例如 docker run .... -v /data/flume/file_log:~/.flume/file_log .... 
