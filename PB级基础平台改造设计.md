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



## 基础平台BUG修复



