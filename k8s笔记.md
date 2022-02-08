# k8s笔记

## kubectl命令行工具

```shell
## 基本命令语法
$ kubectl [command] [TYPE] [NAME] [flags] 

##（1）comand：指定要对资源执行的操作，例如 create、get、describe 和 delete。
##（2）TYPE：指定资源类型，资源类型是大小写敏感的，开发者能够以单数、复数和缩略的形式。
##（3）NAME：指定资源的名称，名称也大小写敏感的。如果省略名称，则会显示所有的资源。
##（4）flags：指定可选的参数。例如，可用-s 或者–server 参数指定 Kubernetes API server 的地址和端口。

##获取帮助
$ kubectl [command] [TYPE] [NAME] [flags] --help

##例如
$ kubectl create deployment nginx --image=nginx 
$ kubectl expose deployment nginx --port=80 --type=NodePort 
$ kubectl get pod,svc #pod 信息以及端口
$ kubectl get cs #节点状态
$ kubectl apply -f xxx.yaml #启动yaml
```



## YAML文件基本知识

### **1.YAML 介绍** 

YAML ：仍是一种标记语言。为了强调这种语言以数据做为中心，而不是以标记语言为重点。 

YAML 是一个可读性高，用来表达数据序列的格式。 

### **2.YAML 基本语法** 

\* 使用空格做为缩进 

\* 缩进的空格数目不重要，一般来说开头缩进2个空格，只要相同层级的元素左侧对齐即可 

\* 低版本缩进时不允许使用 Tab 键，只允许使用空格 

\* 字符后缩进一个空格，比如冒号逗号后面

\* 使用 --- 表示新的yaml的开始

\* 使用#标识注释

### **3.YAML 结构**

apiVersion 。。。。。 template 控制器定义 例如pod

template 。。。。。 结束 被控制对象（容器）

```yaml
apiVersion: apps/v1 #API版本
kind: DaemonSet # 资源类型 例如pod
metadata: #资源元数据
  name: kube-flannel-ds-amd64 #元数据对象名字，自由取名
  namespace: kube-system #元数据对象的命名空间，由我们自己定义
  labels:
    tier: node
    app: flannel
spec: #资源规格
  selector: # 标签选择器
    matchLabels:
      app: flannel
  template: #pod模板
    metadata:
      labels: 
        tier: node
        app: flannel
    spec:
      affinity:
        nodeAffinity:
          requiredDuringSchedulingIgnoredDuringExecution: #硬亲和性
            nodeSelectorTerms:
              - matchExpressions:
                  - key: beta.kubernetes.io/os
                    operator: In
                    values:
                      - linux
                  - key: beta.kubernetes.io/arch
                    operator: In
                    values:
                      - amd64
      hostNetwork: true
      tolerations:
      - operator: Exists
        effect: NoSchedule
      serviceAccountName: flannel
      initContainers:
      - name: install-cni
        image: lizhenliang/flannel:v0.11.0-amd64 
        command:
        - cp
        args:
        - -f
        - /etc/kube-flannel/cni-conf.json
        - /etc/cni/net.d/10-flannel.conflist
        volumeMounts:
        - name: cni
          mountPath: /etc/cni/net.d
        - name: flannel-cfg
          mountPath: /etc/kube-flannel/
      containers: #容器配置
      - name: kube-flannel #容器名称
        image: lizhenliang/flannel:v0.11.0-amd64 #容器使用镜像 
        command:
        - /opt/bin/flanneld
        args:
        - --ip-masq
        - --kube-subnet-mgr
        resources:
          requests:
            cpu: "100m"
            memory: "50Mi"
          limits:
            cpu: "100m"
            memory: "50Mi"
        securityContext:
          privileged: false
          capabilities:
             add: ["NET_ADMIN"]
        env:
        - name: POD_NAME
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: POD_NAMESPACE
          valueFrom:
            fieldRef:
              fieldPath: metadata.namespace
        volumeMounts:
        - name: run
          mountPath: /run/flannel
        - name: flannel-cfg
          mountPath: /etc/kube-flannel/
      volumes:
        - name: run
          hostPath:
            path: /run/flannel
        - name: cni
          hostPath:
            path: /etc/cni/net.d
        - name: flannel-cfg
          configMap:
            name: kube-flannel-cfg

```



### **4.快速编写YAML 文件**

1.kubectl create 去生成一个yaml文件 而不执行 修改后执行 （并未部署资源）

```shell
kubectl create deployment web --image=nginx -o yaml --dry-run > example.yaml
```

2.kubectl get 导出yaml 文件进行修改 运行（已经有类似的部署资源）

```shell
kubectl get deploy web nginx -o=yaml > example2.yaml
```



## pod基本知识

### 1. pod概述

- ​	最小单元的pod
- ​	包含多个容器（一组容器的集合）
- ​	一个pod的容器共享同个网络
- ​	pod是短暂的（重启ip会变）



### 2.存在的意义

- ​	创建容器使用docker，一个docker对应的是一个容器，一个容器里面有一个进程，一个容器里运行的是一个应用程序（多个不方便管理）。
- ​	pod是一个多进程的设计，可以运行多个应用程序，一个pod有多个容器，一个容器里运行一个应用程序。
- ​	pod存在为了亲密性应用，网络之间调用，两个应用进行交互，两应用之间需要频繁调用。

​		

### 3.pod实现机制

共享网络

​	容器本身是相互隔离的，通过linux中的 namespace或者group隔离。

​	前提条件：多个容器处于同个namespace中。

​	默认会创建一个pause（info） 根容器，应用容器创建后会加入到info容器中，这样就可以在同个namespace中。

共享存储

​	引入了数据卷 volume概念，使用数据卷进行持久化存储。



### 4.镜像拉取策略

​	imagePullpolicy：

- alaways 每次创建都会拉取一次
- IfNotPresent 默认值，不存在以后拉取
- Never 从来不会主动拉取	 



### 5.资源限制

```shell
Cpu : 1核心等于1000m 底层是docker里做到的不是pod去做到的

resources:

  request: #调度大小

    memory : "128Mi"

    cpu: "250m"

  limit: #最大限制

    memory : "128Mi"

    cpu: "500m"
```



### 6.重启策略

restartPolicy:

- ​	Alaways: 当容器终止退出以后总是重启容器，默认策略
- ​	OnFailure: 当容器异常退出（退出状态码非0）时，才重启容器
- ​	Never: 当容器终止日退出，从不重启容器



### 7.健康检查

应用层面的健康检查

- livenessProbe 存活检查 如果检查失败，会杀死容器，根据pod的restartPolicy来操作
- readinessProbe 就绪检查 如果检查失败 kubernetes会把pod从service endpoints 中剔除



Probe 支持一下三种检查方法

- httpGet 发送http请求返回200-400范围状态码为成功
- exec 执行shell命令返回状态码是0位成功
- tcpSocket 发起TCP Socket 建立成功



### 8.pod创建流程

​	master节点 ==> API Server create pod ==>（信息存储到）etcd ==> Scheduler调度 API Server监听是否有新的pod创建 分配pod到节点，存到信息到etcd中 ==> 到node节点 ==> 通过kubelet 通过 API Server查看 并读取etcd拿到分配给该节点的pod ==> docker run



### 9.影响pod调度属性

- pod的资源限制会对pod的调度产生影响，会根据requests找到有足够的node进行调度。

- 节点选择器标签(nodeSelector: env_role)影响pod调度 `kubectl label node node1 env_role=prod` 进行给节点打标签

- 污点 Taint: 节点不做普通分配调度，**是节点属性** 不是pod属性，其他三点是pod属性

  ​	场景：专用节点，配置特点硬件节点，基于Taint驱逐

  ​	查看污点情况命令 `kubectl describe node nodename | grep Taint`

  ​	值有三个 NoSchedule：一定不被调度  PerferNoSchedule：尽量不被调度  NoExecute：不会调度，并且还会驱逐Node已有的pod

  ​	添加污点命令: `kubectl taint node nodename key = value`

  ​	污点容忍：tolerations  key + operator + value + effect 控制

- 节点亲和性 nodeAffinity 

  ​	具体参数参照官方文件 

  ​	常用操作符 In NotIn Exists Gt Lt DoesNotExists 

  ​	硬亲和性 约束条件必须满足 key + 操作符 + values 进行约束 不满足等待

  ​	软亲和性  尝试满足  key + 操作符 + values 进行约束



## controller基础知识

### 1.什么是controller

- 集群上管理和运行容器的对象，实际存在的。
- 无状态应用部署。
- 有状态的应用部署。
- 定时任务
- 。。。

### 2.pod和controller的关系

Pod是通过Controller实现应用的运维，比如伸缩，滚动升级等。

Pod和controller是通过labels标签来建立关系。selector.MatchLable.app labels.app 建立

### 3.Deployment控制器应用场景

- 部署无状态应用 。例如：web服务，微服务等等 
- 管理Pod和ReplicaSet。
- 部署，滚动升级等功能。

### 4.Deployment控制器部署应用

`kubectl expose deployment web --port=80 --type-NodePoint --target-point=80 --name=web1 -0 yaml >web1.yaml`

`kubectl apply -f web1.yaml`

### 5.升级回滚

### 6.弹性伸缩
