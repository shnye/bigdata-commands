# k8s笔记

#  通过kubeadm 安装

## 1.1 环境设置

```shell
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=ens33
UUID=3daa5631-e489-4a08-b25a-6b65a601663b
DEVICE=ens33
ONBOOT=yes
IPADDR=192.168.102.131
GATEWAY=192.168.102.2
NETMASK=255.255.255.0
DNS1=8.8.8.8
IPV6_PEERDNS=yes
IPV6_PEERROUTES=yes
IPV6_PRIVACY=no

```



```shell
# 关闭防火墙：
$ systemctl stop firewalld 
$ systemctl disable firewalld 
 
# 关闭 selinux： 
$ sed -i 's/enforcing/disabled/' /etc/selinux/config # 永久 
$ setenforce 0 # 临时

# 关闭 swap： 
$ swapoff -a # 临时
$ vim /etc/fstab # 把swap 注释掉

# 主机名： 
$ hostnamectl set-hostname <hostname> 

# 在 master 添加 hosts： 
$ cat >> /etc/hosts << 
EOF 
192.168.102.131 k8s-master 
192.168.102.132 k8s-node1 
192.168.102.133 k8s-node2 
EOF 

# 将桥接的 IPv4 流量传递到 iptables 的链：
$ cat > /etc/sysctl.d/k8s.conf << 
EOF 
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1 
EOF 
$ sysctl --system 

# 生效 6.7 时间同步： 
$ yum install ntpdate -y 
$ ntpdate time.windows.com
```



## 1.2 所有节点安装 Docker/kubeadm/kubelet 

```shell
#Kubernetes 默认 CRI（容器运行时）为 Docker，因此先安装 Docker。 
#（1）安装 Docker 
$ wget https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo -O /etc/yum.repos.d/docker-ce.repo 
$ yum -y install docker-ce-18.06.1.ce-3.el7 
$ systemctl enable docker && systemctl start docker $ docker --version 

#（2）添加阿里云 YUM 软件源 设置仓库地址 
$ vim /etc/docker/daemon.json

{ "registry-mirrors": ["https://b9pmyelo.mirror.aliyuncs.com"] }


#（3）添加yum源  
$ vim /etc/yum.repos.d/kubernetes.repo

[kubernetes] 
name=Kubernetesbaseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64 
enabled=1 
gpgcheck=0 
repo_gpgcheck=0 
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg 
https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
```



```shell
#安装kubelet kubeadm kubectl
yum install -y kubelet-1.18.0 kubeadm-1.18.0 kubectl-1.18.0
```



## 1.3 master节点初始化

```shell
#在master节点执行
kubeadm init 
--apiserver-advertise-address=192.168.102.131  #主机地址
--image-repository registry.aliyuncs.com/google_containers  #设置阿里云地址
--kubernetes-version v1.18.0 #版本
--service-cidr=10.96.0.0/12 #不冲突即可
--pod-network-cidr=10.244.0.0/16 #不冲突即可

#若出现以下错误需在所有节点执行
#[ERROR FileContent--proc-sys-net-ipv4-ip_forward]: /proc/sys/net/ipv4/ip_forward contents are not se

sysctl -w net.ipv4.ip_forward=1

#执行init成功后 在master节点执行
#To start using your cluster, you need to run the following as a regular user:

mkdir -p $HOME/.kube 

sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config 

sudo chown $(id -u):$(id -g) $HOME/.kube/config 
```



## 1.4 worker节点加入集群

```shell
#worker节点执行 加入集群
kubeadm join 192.168.102.131:6443 --token q66qpz.svo510amnlr21rld \
    --discovery-token-ca-cert-hash sha256:5570d2e88820c460582fd7ce71e03fa9d0db69798be00c11da2ef4599fc9dc80


# 验证是否加入
$ kubectl get nodes
```



## 1.5 安装网络插件

```shell
#安装pod网络插件 CNI
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```



## 1.6 集群测试

```shell
#测试k8s集群
$ kubectl create deployment nginx --image=nginx 
$ kubectl expose deployment nginx --port=80 --type=NodePort 
$ kubectl get pod,svc
```



#  k8s基础概念



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

deployment是最常用的控制器（无状态）。

### 3.Deployment控制器应用场景

- 部署无状态应用 。例如：web服务，微服务等等 
- 管理Pod和ReplicaSet。
- 部署，滚动升级等功能。

### 4.Deployment控制器部署应用

`kubectl expose deployment web --port=80 --type-NodePoint --target-point=80 --name=web1 -0 yaml >web1.yaml`

`kubectl apply -f web1.yaml`

### 5.升级回滚

应用升级

- ​	通过image:image_name:version 指定版本
- ​	`kubectl set image deployment web nginx=nginx:1.15` 进行升级，部署好新的版本以后才会停止老版本
- ​	`kubectl rollout status deployment web` 查看应用升级状态
- ​	`kubectl rollout history deployment web` 查看应用升级的历史版本



应用回滚

- ​	`	kubectl rollout undo deployment web` 回滚到上个版本
- ​	`		kubectl rollout undo deployment web --to-revision=2`  回滚到指定版本

### 6.弹性伸缩

​	`Kubectl scale deployment web --replicas=10` 弹性伸缩



## Service

定义一组pod的访问规则（负载均衡）

防止pod失联（服务发现）



### 1.pod和service 关系

根据label 和selector标签建立关联

service 虚拟ip（vip ）通过虚拟ip进行访问



### 2.常用的service类型

ClusterIP：集群内部进行使用 ，例如：前后端内部访问，默认值

NodePort：对外访问应用使用 例如：nginx

LoadBalancer 对外访问应用使用，可以连接公有云，暴露到公网 暂时没搞懂



## 不同Controller部署

### 1.无状态

- 认为pod都是一样的

- 没有顺序的要求

- 不用考虑在哪个node上运行

- 随意进行伸缩和扩展

  

### 2.有状态

- 无状态的因素都需要考虑到。
- 让每个pod都是独立的，保持pod的启动顺序，唯一性。
- 通过唯一的网络标识符，持久存储，有序的特点。
- 比如mysql的主从。



### 3.部署有状态应用

clusterIP要设置为none 

控制器 StatefulSet



### 4.deployment，statefulset区别

根据主机名+一定规则生成域名

每个pod都会有唯一的主机名 格式：主机名称.service名称.名称空间.svc.cluster.local



### 5.守护进程DeamonSet

在每个node上运行一个pod，新加入的node也同样运行在一个pod

例如在每个节点安装数据采集工具



### 6.一次性，定时任务

一次性任务

apiVersion: batch:v1

kind: job

backoffLimit: 4 失败重试次数

结束后

kubectl delete -f job.yaml 删除任务



定时任务

可以使用cron 表达式设置执行频率 例如 schedule:"*/1 * * * *"

apiVersion: batch:v1

kind: cronjob



kubectl get cronjobs 获取定时任务信息



## 配置管理

### 1.secret

作用：对数据进行加密，存储到etcd里，让pod容器以外在volume的方式进行访问

场景: 凭证

```yaml
apiVersion: v1

kind: Secret

metadata:

	name: mysecret

type: Opaque

data:

	username: xxxxx

	password: yyyyyy
```

步骤：1创建secret加密(base64)数据 ——> kubectl create -f secret.ymal ——> 查看 kubectl get secret

使用1: 以变量的形式挂载到pod容器中env.valuefrom.secretKeyRef.name.key，在容器中echo $key 即可获取到变量。

使用2：以volume的方式挂载 

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mypod
spec:
  containers:
  - name: nginx
    image: nginx
    volumeMounts:
    - name: foo
      mountPath: "/etc/foo"
      readOnly: true
  volumes:
  - name: foo
    secret:
      secretName: mysecret
```



### 2.configMap

数据不加密存储到etcd 让pod以变量或者volume挂载到容器中

场景：配置文件

步骤：

```shell
1 创建configMap 

kubectl create configmap redis-config --from-file=reidis.properties

kubectl get cm

kubectl describe cm redis-config

#变量方式
apiVersion: v1
kind: ConfigMap
metadata:
  name: myconfig
  namespace: default
data:
  special.level: info
  special.type: hello

2 使用
#volumes 方式
volumes:
  - name: config-volume
    创建configMap:
      name: redis-config

#变量方式 
env:
        - name: LEVEL
          valueFrom:
            configMapKeyRef:
              name: myconfig
              key: special.level
        - name: TYPE
          valueFrom:
            configMapKeyRef:
              name: myconfig
              key: special.type
```

  

## K8s集群安全机制

### 1.概述

（1）访问k8s集群的时候，需要经过三个步骤完成操作

- 认证
- 鉴权（授权）
- 准入控制

（2）进行访问时候，过程中都需要经过apiserver，做统一协调，访问过程选中需要证书，token，或者用户名+密码，如果要访问pod还需要一些serviceAccount。



### 2.认证

传输安全：对外不暴露8080端口 只能内部访问，对外统一使用6443端口

认证：客户端身份认证常用方式：1 https证书认证，基于ca证书  2 基于http token识别用户 3 http认证 用户名+密码 



### 3.鉴权（授权）

RBAC的方式进行鉴权操作

基于角色的访问控制：定义角色 + 绑定角色

原理测试：

二进制搭建集群进行测试比较方便

```yaml
# rbac-role.yaml
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  namespace: ctnrs
  name: pod-reader
rules:
- apiGroups: [""] # "" indicates the core API group
  resources: ["pods"]
  verbs: ["get", "watch", "list"]

#创建角色
kubectl apply -f rbac-role.yaml

#rbac-rolebinding.yaml
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: read-pods
  namespace: roletest
subjects:
- kind: User
  name: lucy # Name is case sensitive
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role #this must be Role or ClusterRole
  name: pod-reader # this must match the name of the Role or ClusterRole you wish to bind to
  apiGroup: rbac.authorization.k8s.io

#绑定角色
kubectl apply -f rbac-rolebinding.yaml

```

```shell
#使用证书识别身份，需要把TSL下K8s的ca证书复制过来
cat > mary-csr.json <<EOF
{
  "CN": "mary",
  "hosts": [],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CN",
      "L": "BeiJing",
      "ST": "BeiJing"
    }
  ]
}
EOF

cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=kubernetes mary-csr.json | cfssljson -bare mary 

kubectl config set-cluster kubernetes \
  --certificate-authority=ca.pem \
  --embed-certs=true \
  --server=https://	IP:6443 \
  --kubeconfig=mary-kubeconfig
  
kubectl config set-credentials mary \
  --client-key=mary-key.pem \
  --client-certificate=mary.pem \
  --embed-certs=true \
  --kubeconfig=mary-kubeconfig

kubectl config set-context default \
  --cluster=kubernetes \
  --user=mary \
  --kubeconfig=mary-kubeconfig

kubectl config use-context default --kubeconfig=mary-kubeconfig

```



### 4.准入控制

就是准入控制器的列表，如果列表中有请求的内容，则通过，没有则拒绝。



## Ingress

​	把端口号对外暴露 Service 里面的NodePort，存在一些缺陷，意味着每个端口只能使用一次，一个端口对应一个应用，实际访问都是用域名进行跳转。ingress就是为了弥补nodePort不足而产生的。

​	Ingress也是Controller，非官方自带，需要自己部署，这里选择官方维护的nginx控制器。

​	可以理解为 pod+SVC 之前在加上一个Ingress进行访问控制

### 1.Ingress与pod的关系

pod和ingress通过service关联。

ingres作为统一入口，由service进行统一关联。



### 2.ingress的使用

- 部署ingress Controller
- 创建ingress规则

```yaml
#ingress-controller.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---

kind: ConfigMap
apiVersion: v1
metadata:
  name: nginx-configuration
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
kind: ConfigMap
apiVersion: v1
metadata:
  name: tcp-services
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
kind: ConfigMap
apiVersion: v1
metadata:
  name: udp-services
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nginx-ingress-serviceaccount
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx

---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRole
metadata:
  name: nginx-ingress-clusterrole
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - endpoints
      - nodes
      - pods
      - secrets
    verbs:
      - list
      - watch
  - apiGroups:
      - ""
    resources:
      - nodes
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - services
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - ""
    resources:
      - events
    verbs:
      - create
      - patch
  - apiGroups:
      - "extensions"
      - "networking.k8s.io"
    resources:
      - ingresses
    verbs:
      - get
      - list
      - watch
  - apiGroups:
      - "extensions"
      - "networking.k8s.io"
    resources:
      - ingresses/status
    verbs:
      - update

---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: Role
metadata:
  name: nginx-ingress-role
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
      - pods
      - secrets
      - namespaces
    verbs:
      - get
  - apiGroups:
      - ""
    resources:
      - configmaps
    resourceNames:
      # Defaults to "<election-id>-<ingress-class>"
      # Here: "<ingress-controller-leader>-<nginx>"
      # This has to be adapted if you change either parameter
      # when launching the nginx-ingress-controller.
      - "ingress-controller-leader-nginx"
    verbs:
      - get
      - update
  - apiGroups:
      - ""
    resources:
      - configmaps
    verbs:
      - create
  - apiGroups:
      - ""
    resources:
      - endpoints
    verbs:
      - get

---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: nginx-ingress-role-nisa-binding
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: nginx-ingress-role
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: ingress-nginx

---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: nginx-ingress-clusterrole-nisa-binding
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: nginx-ingress-clusterrole
subjects:
  - kind: ServiceAccount
    name: nginx-ingress-serviceaccount
    namespace: ingress-nginx

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-ingress-controller
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/name: ingress-nginx
      app.kubernetes.io/part-of: ingress-nginx
  template:
    metadata:
      labels:
        app.kubernetes.io/name: ingress-nginx
        app.kubernetes.io/part-of: ingress-nginx
      annotations:
        prometheus.io/port: "10254"
        prometheus.io/scrape: "true"
    spec:
      hostNetwork: true
      # wait up to five minutes for the drain of connections
      terminationGracePeriodSeconds: 300
      serviceAccountName: nginx-ingress-serviceaccount
      nodeSelector:
        kubernetes.io/os: linux
      containers:
        - name: nginx-ingress-controller
          image: lizhenliang/nginx-ingress-controller:0.30.0
          args:
            - /nginx-ingress-controller
            - --configmap=$(POD_NAMESPACE)/nginx-configuration
            - --tcp-services-configmap=$(POD_NAMESPACE)/tcp-services
            - --udp-services-configmap=$(POD_NAMESPACE)/udp-services
            - --publish-service=$(POD_NAMESPACE)/ingress-nginx
            - --annotations-prefix=nginx.ingress.kubernetes.io
          securityContext:
            allowPrivilegeEscalation: true
            capabilities:
              drop:
                - ALL
              add:
                - NET_BIND_SERVICE
            # www-data -> 101
            runAsUser: 101
          env:
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
          ports:
            - name: http
              containerPort: 80
              protocol: TCP
            - name: https
              containerPort: 443
              protocol: TCP
          livenessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            initialDelaySeconds: 10
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 10
          readinessProbe:
            failureThreshold: 3
            httpGet:
              path: /healthz
              port: 10254
              scheme: HTTP
            periodSeconds: 10
            successThreshold: 1
            timeoutSeconds: 10
          lifecycle:
            preStop:
              exec:
                command:
                  - /wait-shutdown

---

apiVersion: v1
kind: LimitRange
metadata:
  name: ingress-nginx
  namespace: ingress-nginx
  labels:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/part-of: ingress-nginx
spec:
  limits:
  - min:
      memory: 90Mi
      cpu: 100m
    type: Container

```



```yaml
#ingress.yaml
#ingress 规则配置
---
# http
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: example-ingress
spec:
  rules:
  - host: example.ctnrs.com
    http:
      paths:
      - path: /
        backend:
          serviceName: web
          servicePort: 80

---
# https
apiVersion: networking.k8s.io/v1beta1
kind: Ingress
metadata:
  name: tls-example-ingress
spec:
  tls:
  - hosts:
    - sslexample.ctnrs.com
    secretName: secret-tls
  rules:
    - host: sslexample.ctnrs.com
      http:
        paths:
        - path: /
          backend:
            serviceName: web
            servicePort: 80
```



## 补充port参数

### port ---service

port是k8s集群内部访问service的端口，即通过clusterIP: port可以访问到某个service

### nodePort --- 集群

nodePort是外部访问k8s集群中service的端口，通过nodeIP: nodePort可以从外部访问到某个service。

### targetPort ---pod

targetPort是pod的端口，从port和nodePort来的流量经过kube-proxy流入到后端pod的targetPort上，最后进入容器。

### containerPort ----pod内部

containerPort是pod内部容器的端口，targetPort映射到containerPort。



## helm

引入：之前部署过程 deployment + service + ingress 部署微服务项目时，不适合，需要维护很多yaml文件。

- 使用helm可以把这些yaml作为一个整体管理
- 实现yaml高效复用
- 使用helm可以进行应用级别的版本管理

helm是一个k8s的包管理工具，例如apt yum 可以很方便的将之前打好的yaml文件部署到k8s上

### 1.helm三个重要概念

- helm 是一个命令行客户端工具，用于chart的创建打包发布管理
- Chart 把yaml进行打包，是yaml的集合，应用描述。
- Release 基于chart部署实体，应用级别的版本管理



### 2.V3版本新特性

- 删除了tiller【连接helm和kube-apiserver】（架构变化）==>通过kube-config进行连接
- realease可以再不同命名空间重用
- 可以将chart推导docker仓库中



### 3.安装和配置

1.安装helm

下载helm安装压缩文件，上传到服务器

解压helm压缩文件，把解压之后的目录移动到/usr/bin目录下即可



2配置helm

添加仓库 helm repo add 仓库名称(随便写) 仓库地址（用阿里云镜像即可）

helm update 更新

Helm repo list 查看

删除仓库 helm remove 仓库名称



### 简单使用

1.搜索应用

helm search repo 名称



2.根据搜索内容选择进行安装

helm install 名称 搜索后应用名称

helm list  

helm status 名称 查看状态



3需要暴露端口则修改service yaml 文件 type: ClusterIP ==>NodePort

kubectl edit svc name



### 4.自己创建chart

1.使用命令创建chart ：helm create chart名称，后进入该chart名称文件夹

2.把yaml文件放到templates文件夹下，Chartyaml 当前chart属性配合信息，values.yaml 可以使用全局变量

3 helm install name chart名称 安装chart

4 应用升级 helm upgrade chart名称 chart文件夹



### 5.yaml高效复用

通过values.yaml 中配置变量进行操作，在具体的yaml文件中定义变量值

yaml文件大题有几个地方不同：

- image
- tag
- label
- port
- replicas

步骤

- 在template中使用values.yaml变量（注意有个空格）
- 通过表达式形式使用全局变量：{{ .Values.变量名}} 特殊：获取版本名称： {{ .Release.Name}}
- `helm install --dry-run` 名称 chart文件夹 查看yaml



## 持久化储存

数据卷 emptydir 是本地数据卷，pod重启数据就不存在了，需要进行持久化存储

### 1.nfs

nfs，网络储存：只要存放数据的服务器还在，数据就还存在

安装nfs  `yum install -y nfs-utils`

设置挂载路径 `vim /etc/exports` 下设置 /data/nfs *{rw,no_root_squash}

挂载路径需要创建出来 `mkdir /data/nfs`

nfs服务器启动nfs服务 `systemctl start nfs`

在k8s集群node挂载nfs（各个节点安装nfs）

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-dep1
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        volumeMounts:
        - name: wwwroot
          mountPath: /usr/share/nginx/html
        ports:
        - containerPort: 80
      volumes:
        - name: wwwroot
          nfs:
            server: 192.168.44.134
            path: /data/nfs
```

### 2PV和PVC

pv：持久化存储，对存储资源进行抽象，对外提供可以调用的地方（生产者）

PVC：用于调用，不需要关系内部实现细节（消费者）

 部署流程：应用部署==>定义pvc（绑定pv）==>定义pv（数据存储服务器ip，路径，会根据容量，匹配模式进行）



```yaml
#pv
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 5Gi
  accessModes:
    - ReadWriteMany
  nfs:
    path: /k8s/nfs
    server: 192.168.44.134
    
#PVC  
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-dep1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx
        volumeMounts:
        - name: wwwroot
          mountPath: /usr/share/nginx/html
        ports:
        - containerPort: 80
      volumes:
      - name: wwwroot
        persistentVolumeClaim:
          claimName: my-pvc

---

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 5Gi
```





## 集群资源监控

### 监控指标：

- ​	集群监控（节点资源利用率，节点数，运行的pods ）
- ​	pod监控（容器指标，应用程序）
- ​	。。。。。。



### 监控平台搭建方案

Prometheus + Grafana

Prometheus ：

- ​	开源
- ​	监控，报警，数据库
- ​	以http协议周期性抓取被监控组建状态
- ​	不需要复杂的集成过程，使用http接口接入就行了



DaemSet 守护进程 + rbac访问权限 + configmap存储相关配置参数 + deploy部署本体 + svc Nodeport暴露端口 

 

Grafana：

- ​	开源的数据分析和可视化工具
- ​	支持多种数据源



deploy + ingress + svc 部署

打开Grafana 配置Prometheus 数据源，选择展示模板即可



## 高可用集群

load balancer 来实现负载均衡 + 高可用 + 检查master节点的状态

node 连接 master 时候连接VIP（虚拟IP）来实现

