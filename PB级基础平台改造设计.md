# 	PB级基础平台改造设计



## 基础平台稳定性和性能提升



#### 场景1：瞬时高并发导致NanmeNode不可用问题

情景： buffercurrent还没刷写完到磁盘。bufReady已经满了，新来的请求就没法写入到内存

```java
//跳转流向：FSEditLog(editLogStream.write(op)) 
//-->EditLogOutputStream(EditlogFileOutputStream 里实现write方法(doubleBuf.writeOp(op))) --> EditsDoubleBuffer 中赋值


//EditsDoubleBuffer.java 
private TxnBuffer buffercurrent;
private TxnBuffer bufReady;

//进行构造 赋值到双缓存
public EditsDoubuleBuffer(int deafultBufferSize)
  
//其中缓存的大小都是通过下面类调用构造函数进行赋值
//QuorumOutputStream
public QuorumOutputStream(AsyncLoggerSet loggers,
		Long txId, int outputBufferCapacity, int writeTimeoutMs) throws IOException{ 
  super();
  //赋值
	this.buf = new EditsDoubleBuffer (outputBufferCapacity);                                                                                  	this.loggers = loggers; 	
	this.segmentTxId = txId;
	this.writeTimeoutMs = writeTimeoutMs;
}

//QuorumJournalManager.java 
private int outputBufferCapacity = 512 * 1024；
new QuorumOutputStream(loggers,txId,outputBufferCapacity,writeTxnsTimeoutMS)

```

buffercurrent 默认设置为512K（写死不可配）百台以内不用动。可以改成5M，原理在于内存写翻了10倍（一条一条写）时间也为10倍，刷写到磁盘的时候是一批次全部写，时间不到10倍，可以大大缓解。

风险：调大buffercurrent会增加丢失数据的数量（断电，内存数据丢失）

```java
//步骤：QuorumJournalManager.java 把private int outputBufferCapacity = 512 * 1024；注释变成值
private int outputBufferCapacity

//在构造函数内增加
this.outputBuffencapacity = conf. getInt(
	DFSConfigKeys.DFS_NAMENDDE_BUFFER_KEY,
	DFSConFiдKeys.DFS_NAMENODE_BUFFER_VALUE_DEFALUT);


```
#### 场景2：datanode慢启动问题

​	情景：集群越扩越大以后，机柜不够，带宽也不够。搬机房以后重启发现datanode等了8-10分钟才注册上来。

​	原因分析：datanode每隔一段时间会在磁盘上记录时间以及使用多少存储空间。重启时候会和namenode存储当前存储信息，可以达到快速启动，但是如果没有这个记录文件，就会起一个线程对所有磁盘都要扫描一遍在汇报，如果数据量比较大，就需要扫描很久。

```java
//BlockPoolSlice.java 会执行构造函数






//如果当前时间 - 最后一条记录时间 就会认为该值过期，就会重新进行扫描


//修改过期时间改变硬编码，做成可配置。
//但是可能会暂时造成可用空间暂时不准确，在下次datanode进行块汇报时，会进行更新
```



## 基础平台BUG修复



#### 场景1：journalnode oom错误导致datanode 退出

​	双缓存刷写到Journalnode磁盘时，如果一直没有超过半数成功，或者失败，或者全部响应，超过了20秒，就会抛出TimeouException异常，调用方法的捕获TimeouException转成IO异常。直到logStream.flush()这个方法也抛出IO异常，被捕获这时候会打印出LOG.fatal 灾难型的错误，然后执行System.exit(status)导致namenode强制退出。

​	有可能是有namenode full GC 导致，需要判断是否full GC 要扣除这部分的时间 ，在后面的版本有修复此问题。

```java
//报错代码，点击Logstream.fLush()进入
	//把数据写到本地的磁盘journalnode
			Logstream.fLush();
	}
} catch (IOException ex) {
  synchronized (this) {
    final String msg =
			"Could not sync enough journals to persistent storage. " +"Unsynced transactions: "+ (txid - synctxid); 
    //fata
		//灾难型的错误
    LOG. fatal(msg, new Exception() );
		synchronized(journalSetLock) {
      IOUtils.cleanup(LOG, journalSet); 
    }
//TODO
terminate ( status: 1, msg);
	}
}
......
  System.exit(status)
```



## 优秀代码鉴赏

#### 0.8Kafak生产者原码改造

0.8版本生产者提交时一条一条处理，效率不够高，0.9时采用批处理解决了此问题。 

分段加锁 + double check +读写分离,高性能数据结构 + 内存池





## 管理和监控
