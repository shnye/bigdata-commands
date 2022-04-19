# Ambari部署文档

# 1. 介绍

## 1.1 Ambari

- Ambari 跟 Hadoop 等开源软件一样，也是 Apache Software Foundation 中的一个项目，并且是顶级项目。就 Ambari 的作用来说，就是创建、管理、监视 Hadoop 的集群，但是这里的 Hadoop 指的是 Hadoop 整个生态圈（例如 Hive，Hbase，Sqoop，Zookeeper 等）， 而并不仅是特指 Hadoop。用一句话来说，Ambari 就是为了让 Hadoop 以及相关的大数据软件更容易使用的一个工具。
- Ambari 自身也是一个分布式架构的软件，主要由两部分组成：Ambari Server 和 Ambari Agent。简单来说，用户通过 Ambari Server 通知 Ambari Agent 安装对应的软件；Agent 会定时地发送各个机器每个软件模块的状态给 Ambari Server，最终这些状态信息会呈现在 Ambari 的 GUI，方便用户了解到集群的各种状态，并进行相应的维护。

## 1.2 HDP

- HDP是hortonworks的软件栈，里面包含了hadoop生态系统的所有软件项目，比如HBase,Zookeeper,Hive,Pig等等。

## 1.3 HDP-UTILS

- HDP-UTILS是工具类库。

# 2. 准备工作

## 2.1 Ambari、HDP版本介绍

- Ambari 2.7.3+HDP-3.1.0
- 版本支持文档查询https://supportmatrix.hortonworks.com/。

## 2.2 搭建环境准备

### 2.2.1 软件要求

组件描述虚拟操作系统Centos7.8Ambari2.7.3HDP3.1.0.0HDP-GPL3.1.0.0HDP-UTILS1.1.0.22MySQL5.7.26OracleJDK8JDK 1.8.0_144X86X86-64

- Ambari 2.7.3安装包下载地址：https://docs.hortonworks.com/HDPDocuments/Ambari-2.7.3.0/bk_ambari-installation/content/ambari_repositories.html
- HDP 3.1.0.0、HDP-GPL-3.1.0.0 和 HDP-UTILS 1.1.0.22安装包下载地址：https://docs.hortonworks.com/HDPDocuments/Ambari-2.7.3.0/bk_ambari-installation/content/hdp_31_repositories.html

## 2.3 集群节点规划准备

HostnameIPFunctions内存磁盘hadoop117192.168.44.117Ambari/HDP packages/Ambari Server 32G1Thadoop118192.168.44.118Compute node32G1Thadoop119192.168.44.119Compute node32G1Thadoop120192.168.44.121Compute node32G1Thadoop121192.168.44.122Compute node32G1T

# 3.环境准备

## 3.1 服务器设置

### 3.1.1 设置hostname

```shell
192.168.44.117 hadoop117
192.168.44.118 hadoop118
192.168.44.119 hadoop119
192.168.44.120 hadoop120
192.168.44.121 hadoop121
```





### 3.1.2 设置免密登录

- hadoop117 ⇒ 117，118，119，120，121的免密登录：

```shell
[root@hadoop117 ~] ssh-keygen -t rsa   # 一路回车
[root@hadoop117 ~] ssh-copy-id hadoop117 # 输入密码
[root@hadoop117 ~] ssh-copy-id hadoop118 # 输入密码
[root@hadoop117 ~] ssh-copy-id hadoop118 # 输入密码
......
```

- hadoop118 ⇒ 117，118，119，120，121的免密登录：

```shell
[root@hadoop118 ~] ssh-keygen -t rsa   # 一路回车
[root@hadoop118 ~] ssh-copy-id hadoop117 # 输入密码
[root@hadoop118 ~] ssh-copy-id hadoop118 # 输入密码
[root@hadoop118 ~] ssh-copy-id hadoop119 # 输入密码
......
```



- hadoop119 120 121按照此依次设置



### 3.1.3 设置阿里开源镜像yum源

- 访问：https://opsx.alibaba.com/mirror
- 按以下步骤执行：

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=ZTI2ZDVjNDE1YjE0NjAzYTQ2ODBjZDFjM2IzZjI1OThfMUs2WEl6M2hYa1RmMndNckxkSnN0MXRyb2dxQzQzOUJfVG9rZW46Ym94Y25EZ29ZaDU0SnBGdHBTdUFYSERsWGRlXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=Njk5Y2Q4ZjhkN2UxNzUzZTE2MjExMWVmMDhjY2ZjODVfWVd1V1pqbVBKSXlRQnFOdnJnNGF1Qk5YZGpITXppeG5fVG9rZW46Ym94Y25UemgzME1WYjdpY3h1QmdQTkN0M1ZCXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

### 3.1.4 安装时间同步服务（ntp）

- 安装：yum install -y ntp

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=YjY1MDM1ODdiNWYxMjJlMTdlMjljN2FlNzliNDk5M2JfNTVwWXd6STl0QVdoNjZQaHdOc1ZJdFFGYUhJUXNBOGJfVG9rZW46Ym94Y25KMWpJVU5hYkRXM0VXZGpwSVBRcDRiXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 启动并查看状态：
- systemctl start ntpd.service
- systemctl status ntpd.service

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MjJkZTliYzcxYTM5ZThmYzY4MWNlZjExOTcwMzYyYzRfTmVJSHJWazBZSlVxUHBTYjdOY1hKNFhETEdFNWRXNENfVG9rZW46Ym94Y25ZUDI0aUgxMmg3ZVFENmM1cTlPaDFnXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 设置开机自启：systemctl enable ntpd.service

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MTk4Y2Y5MzQxNDc3ZjZkNGFmZDk1YWNlZGViZThjMzNfc0tJejYyam5qNWdVdDdBVkp3bE95SlVmUEZVZUpJdEZfVG9rZW46Ym94Y25rYTcxSko4cnozTWhLOVVpR3E3aFRkXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

### 3.1.5 安装JDK

- 解压至/opt/module目录下

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=OTAxMzlmYjA1YzczYzdkYWU5MjgxY2IyNmQwODQ0MGJfODBrd0JGTU40N2xHejBXT0gwRGVJYnVIb2pVeko2clJfVG9rZW46Ym94Y25iTjZCZEVSYkU3ZldkaTdid0s4WDBkXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



- 编写xsync分发脚本

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=NjhjZTQxOWRlN2E1YzYxYjc3Y2UwMGE1N2RlZmNkNzBfM1E3bFBtajNsY2pDWjM1d0pLTVNLUlFPUWg2Y2RLbVpfVG9rZW46Ym94Y244RHRIYkVMVml1Nk5BSUJ2UW1KN3FiXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



- 分发JDK

xsync /opt/module/jdk1.8.0_144



- 配置环境变量(hadoop117-hadoop121)

vim /etc/profile

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=NTBjODQ3MDg0YjUyYWZhMjYwNzAyY2M4YzZlY2NkNjZfQ2dRYzc4dWtLWVdyQjF5WlFnSHZsS2dPdGpXRVRkU0lfVG9rZW46Ym94Y25hNEF3WkR0WXdnWTlYcjBEZWZsSVRjXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=ZjUzOTA5ZjY4MTZhMzFjNDMyOWI5NDBhNjIzNTUxNWJfMnlOeU5JSnVNVmNycmJER256U09jQzlETlN0ckZjUHpfVG9rZW46Ym94Y25Ud2tzU2d0c043eTQ1Z0JSNkw4cHZmXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)





### 3.1.6 安装MySQL

- 官网下载mysql免安装包：mysql-5.7.26-linux-glibc2.12-x86_64.tar.gz
- https://downloads.mysql.com/archives/community/
- https://blog.csdn.net/weixin_38822045/article/details/91447250
- 解压至/usr/local/mysql
- 创建用户及用户组赋予权限

```shell
groupadd mysql
useradd -r -g mysql mysql
chown -R mysql:mysql /usr/local/mysql  
```



- 修改配置文件：/etc/my.cnf 

```shell
[mysqld]



basedir=/usr/local/mysql

datadir=/usr/local/mysql/data

port = 3306

socket=/usr/local/mysql/tmp/mysql.sock



symbolic-links=0

log-error=/usr/local/mysql/log/mysqld.log

pid-file=/usr/local/mysql/tmp/mysqld.pid

sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION'

[client]

default-character-set=utf8

port=3306

socket=/usr/local/mysql/tmp/mysql.sock



[mysql]

default-character-set=utf8



[mysqld]

log-bin=mysql-bin

binlog-format=ROW

server_id=1

max_connections=1000


init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
default-storage-engine=INNODB
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
```



- 创建文件，赋予权限

```shell
mkdir /usr/local/mysql/tmp
mkdir /usr/local/mysql/log
mkdir /usr/local/mysql/data
touch /usr/local/mysql/tmp/mysql.sock
touch /usr/local/mysql/tmp/mysqld.pid
touch /usr/local/mysql/log/mysqld.log
chown -R mysql:mysql /usr/local/mysql/tmp
chown -R mysql:mysql /usr/local/mysql/log
chmod 755  /usr/local/mysql/tmp/*
chmod 755  /usr/local/mysql/log/*
```



- 安全启动数据库

```shell
cd /usr/local/mysql/bin/  

./mysqld --initialize --user=mysql --basedir=/usr/local/mysql--datadir=/usr/local/mysql/data  

./mysqld_safe --user=mysql &  
```

- 查看日志文件，获取数据库默认密码，进入数据库修改密码

```sql
/usr/local/mysql/log/mysqld.log

alter user user() identified by "XXXXXX";

GRANT ALL PRIVILEGES ON *.* TO 'your username'@'%' IDENTIFIED BY 'your password';
```

- 将数据库设置为开机启动

```shell
cp -a /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql  

chkconfig --add mysql  
```



### 3.1.7 安装libtirpc-devel依赖 

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MzEwODNiOWU0MWI5MGViY2JhZWYxYjMwNjE1MTRjMjVfVmQ3SjcwcGhXMUhPODJBM05kbVljZFFZYmFGRWpwQVZfVG9rZW46Ym94Y25NUENxbXI0OGptZnRocUJibGhyNHBQXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

### 3.1.8在mysql数据库创建相应的用户和DB

- 创建ambari数据库及数据库的用户名和密码

```sql
mysql> set global validate_password_policy=0;

mysql> set global validate_password_length=1;

mysql> create database ambari character set utf8;

Query OK, 1 row affected (0.00 sec)

mysql> CREATE USER 'ambari'@'%'IDENTIFIED BY 'ambari_123';

Query OK, 0 rows affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON ambari.* TO 'ambari'@'%';

Query OK, 0 rows affected (0.00 sec)

mysql> FLUSH PRIVILEGES;

Query OK, 0 rows affected (0.01 sec)

```



- 创建hive数据库及hive库的用户名和密码

```sql
mysql> create database hive character set utf8;

Query OK, 1 row affected (0.00 sec)

mysql> CREATE USER 'hive'@'%'IDENTIFIED BY 'hive_123';

Query OK, 0 rows affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON hive.* TO 'hive'@'%';

Query OK, 0 rows affected (0.00 sec)

mysql> FLUSH PRIVILEGES;

Query OK, 0 rows affected (0.01 sec)


```



- 创建oozie数据库及oozie库的用户名和密码

```sql
mysql> create database oozie character set utf8;

Query OK, 1 row affected (0.00 sec)

mysql> CREATE USER 'oozie'@'%'IDENTIFIED BY 'oozie_123';

Query OK, 0 rows affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON oozie.* TO 'oozie'@'%';

Query OK, 0 rows affected (0.00 sec)

mysql> FLUSH PRIVILEGES;

Query OK, 0 rows affected (0.01 sec)
```



# 4.安装Ambari

## 4.1 安装yum相关工具

```shell
[root@hadoop117 ~] yum install yum-utils -y
[root@hadoop117 ~] yum repolist
[root@hadoop117 ~] yum install createrepo -y
```



## 4.2 安装Apache httpd

- 使用yum在线安装httpd：上传下载Ambari，HDP等文件



[root@hadoop117 ~] yum install httpd -y



- 安装完成后，会生成 /var/www/html目录（相当于Tomcat的webapps目录），进入到/var/www/html目录下，创建ambari和hdp目录，用来存放安装文件。

```shell
[root@hadoop117 ~] mkdir /var/www/html/ambari

[root@hadoop117 ~] mkdir /var/www/html/hdp

[root@hadoop117 ~] mkdir /var/www/html/hdp/HDP-UTILS-1.1.0.22

[root@hadoop117 ~] tar -zxvf ambari-2.7.3.0-centos7.tar.gz -C /var/www/html/ambari/

[root@hadoop117 ~] tar -zxvf HDP-3.1.0.0-centos7-rpm.tar.gz -C /var/www/html/hdp/

[root@hadoop117 ~] tar -zxvf HDP-UTILS-1.1.0.22-centos7.tar.gz -C /var/www/html/hdp/HDP-UTILS-1.1.0.22/
```



- 启动httpd服务：

```shell
[root@hadoop117 ~]# systemctl start httpd                # 启动httpd

[root@hadoop117 ~]# systemctl status httpd                # 查看httpd状态

[root@hadoop117 ~]# systemctl enable httpd        # 设置httpd开机自启
```



![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=ODJjYzAxN2RmYTc4MjRmMTBjNGE4OWY2YzI0NWYzZDVfaTFKVzZXU3NpeGxmTmUwcGl3ODRQODM4WTE5TnZ1ZkFfVG9rZW46Ym94Y25ORVk2UTNsN3VHcG5lTmRLd1BXQzVjXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



- 默认端口80，浏览器输入：http://hadoop117:80

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=NDUzMDE5MjQyMzdjZTg0NDNhNjRkYzIwOTc5OTI5NzVfbHFlMDJDS1E3OUYwZ1JMbWFnYXl3ZXp0N3ZPMEllOVdfVG9rZW46Ym94Y25mWGYwTVlDbXpHVFZuN3Y0U09WVHRjXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=YjI4NjE0NmY4ZDljNDA2ZmZiNmMyZjRhYWYxN2JkY2VfUzVYY1hIZm53azNVVjJHcW1PUGdHdDVsb3hXUnRoYXpfVG9rZW46Ym94Y25LTldPWk81THdXN25JOE9ZSWJlVk9mXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

## 4.3 配置本地 Repo

### 4.3.1 配置Ambari

- 下载

wget -O /etc/yum.repos.d/ambari.repo http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.7.3.0/ambari.repo



- 修改配置文件：vim /etc/yum.repos.d/ambari.repo

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=YTQxNjk0ZDM0MWNiNmZhMzljZmMzZjA2MTkwZDQ1MzFfWnhiQUFXYm1jeU9aRFZmWmdLMGtrOFl6NndLYmlKQVJfVG9rZW46Ym94Y25RSDZicW12VnVDb1FkT205TWpzam1mXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



### 4.3.1 配置HDP和HDP-UTILS

- 创建配置文件：[root@hadoop117]# touch /etc/yum.repos.d/HDP.repo

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=NTFlOWQwNGM5NzM2OTIzZDYzZTllM2I1ZjI4ZWI0NzNfSWtGTUZNdEJVUXJKRVFyQ3VtckNna1FpNjlhSnBZYUtfVG9rZW46Ym94Y25hS3VPdkZwVWNxOHVQbFNxU2YwcEZmXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

### 4.3.2 分发Ambari.repo和HDP.repo

- 把ambari.repo HDP.repo分发到各个节点的相同目录下

```shell
[root@yum yum.repos.d] xsync ambari.repo HDP.repo
```



### 4.3.3 生成本地源

- 使用createrepo命令，创建yum本地源（软件仓库），即为存放本地特定位置的众多rpm包建立索引，描述各包所需依赖信息，并形成元数据。

```shell
[root@hadoop117 ~] createrepo /var/www/html/hdp/HDP/centos7/

Spawning worker 0 with 51 pkgs

Spawning worker 1 with 50 pkgs

Spawning worker 2 with 50 pkgs

Spawning worker 3 with 50 pkgs

Workers Finished

Saving Primary metadata

Saving file lists metadata

Saving other metadata

Generating sqlite DBs

Sqlite DBs complete



[root@hadoop117 ~] createrepo /var/www/html/hdp/HDP-UTILS-1.1.0.22/

Spawning worker 0 with 4 pkgs

Spawning worker 1 with 4 pkgs

Spawning worker 2 with 4 pkgs

Spawning worker 3 with 4 pkgs

Workers Finished

Saving Primary metadata

Saving file lists metadata

Saving other metadata

Generating sqlite DBs

Sqlite DBs complete


```





### 4.4 另一种配置本地仓库方法(2.7.4+3.1.4)

#### 4.4.1. 下载安 ambari所 的一些资源

 Cloudera的官网 入到如下网址界面 https://docs.cloudera.com/HDPDocuments/Ambari-2.

7.5.0/bk_ambari-installation/content/ambari_repositories.html

 下载以下的资源包 

Ambari相关安 包 http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.7.4.0/a

mbari-2.7.4.0-centos7.tar.gz

HDP安 http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.1.4.0/HDP-3.1.4.0-c

entos7-rpm.tar.gz

HDP-UTIL安 包 http://public-repo-1.hortonworks.com/HDP-UTILS-1.1.0.22/repos/centos7/HDP

-UTILS-1.1.0.22-centos7.tar.gz

HDP-GPL文件 http://public-repo-1.hortonworks.com/HDP-GPL/centos7/3.x/updates/3.1.4.0/HD

P-GPL-3.1.4.0-centos7-gpl.tar.gz



#### 4.4.2上传到master节点

```Shell
ambari-2.7.4.0-centos7.tar.gz
HDP-3.1.4.0-centos7-rpm.tar.gz
HDP-GPL-3.1.4.0-centos7-gpl.tar.gz
HDP-UTILS-1.1.0.22-centos7.tar.gz

解压


mkdir -p /var/www/html/hdp314-centos7
tar -zxvf ~/soft/ambari-2.7.4.0-centos7.tar.gz -C /var/www/html/hdp314-centos7
tar -zxvf ~/soft/HDP-3.1.4.0-centos7-rpm.tar.gz -C /var/www/html/hdp314-centos7
tar -zxvf ~/soft/HDP-GPL-3.1.4.0-centos7-gpl.tar.gz -C /var/www/html/hdp314-centos7
tar -zxvf ~/soft/HDP-UTILS-1.1.0.22-centos7.tar.gz -C /var/www/html/hdp314-centos7
```



4.4.3配置yum源

```Shell
#配置 yum 源

cd /etc/yum.repos.d/

vim ambari.repo


#文件内容

[CentOS7-HDP]

name=CentOS7-HDP

baseurl=http://bigdata22/hdp314-centos7/HDP/centos7/3.1.4.0-315

gpgcheck=0

enabled=1

[CentOS7-HDP-GPL]

name=CentOS7-HDP-GPL

baseurl=http://bigdata22/hdp314-centos7/HDP-GPL/centos7/3.1.4.0-315

gpgcheck=0

enabled=1

[CentOS7-HDP-UTILS]

name=CentOS7-HDP-UTILS

baseurl=http://bigdata22/hdp314-centos7/HDP-UTILS/centos7/1.1.0.22

gpgcheck=0

enabled=1

[CentOS7-ambari]

name=CentOS7-ambari

baseurl=http://bigdata22/hdp314-centos7/ambari/centos7/2.7.4.0-118

gpgcheck=0

enabled=1





#清楚缓存

yum clean all

#生成缓存

yum makecache

#重新检测

yum repolist
```



# 5. 安装Ambari-Server

## 5.1 hadoop117 节点安装

- 安装ambari-server

```shell
[root@hadoop117 ~] yum install ambari-server

[root@hadoop117 ~] ambari-server setup

Using python  /usr/bin/python

Setup ambari-server

Checking SELinux...

SELinux status is 'disabled'

Customize user account for ambari-server daemon [y/n] (n)? y

Enter user account for ambari-server daemon (root):root                # 用户

Adjusting ambari-server permissions and ownership...

Checking firewall status...

Checking JDK...

[1] Oracle JDK 1.8 + Java Cryptography Extension (JCE) Policy Files 8

[2] Custom JDK

==============================================================================

Enter choice (1): 2                # 选择自定义jdk

WARNING: JDK must be installed on all hosts and JAVA_HOME must be valid on all hosts.

WARNING: JCE Policy files are required for configuring Kerberos security. If you plan to use Kerberos,please make sure JCE Unlimited Strength Jurisdiction Policy Files are valid on all hosts.

Path to JAVA_HOME: /opt/module/jdk1.8.0_144                # jdk安装路径

Validating JDK on Ambari Server...done.

Check JDK version for Ambari Server...

JDK version found: 8

Minimum JDK version is 8 for Ambari. Skipping to setup different JDK for Ambari Server.

Checking GPL software agreement...

GPL License for LZO: https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html

Enable Ambari Server to download and install GPL Licensed LZO packages [y/n] (n)? y

Completing setup...

Configuring database...

Enter advanced database configuration [y/n] (n)? y

Configuring database...

==============================================================================

Choose one of the following options:

[1] - PostgreSQL (Embedded)

[2] - Oracle

[3] - MySQL / MariaDB

[4] - PostgreSQL

[5] - Microsoft SQL Server (Tech Preview)

[6] - SQL Anywhere

[7] - BDB

==============================================================================

Enter choice (1): 3                # 选择安装的mysql

Hostname (localhost): hadoop117                # 配置hostname

Port (3306): 3306                # 默认

Database name (ambari): ambari

Username (ambari): ambri

Enter Database Password (ambari): ambari_123             # 密码不显示

Re-enter password: ambari_123

Configuring ambari database...

Should ambari use existing default jdbc /usr/share/java/mysql-connector-java.jar [y/n] (y)? y

Configuring remote database connection properties...

WARNING: Before starting Ambari Server, you must run the following DDL directly from the database shell to create the schema: /var/lib/ambari-server/resources/Ambari-DDL-MySQL-CREATE.sql                # 此处需注意，启动ambari之前需要执行此句

Proceed with configuring remote database connection properties [y/n] (y)? y

Extracting system views...

ambari-admin-2.7.3.0.139.jar

....

Ambari repo file contains latest json url http://public-repo-1.hortonworks.com/HDP/hdp_urlinfo.json, updating stacks repoinfos with it...

Adjusting ambari-server permissions and ownership...

Ambari Server 'setup' completed successfully.                # 安装成功


```



- 执行上面安装过程中给出的提示

```sql
# 使用root登录，设置允许远程登录

mysql> set global validate_password_policy=0;

Query OK, 0 rows affected (0.00 sec)

mysql> set global validate_password_length=1;

Query OK, 0 rows affected (0.00 sec)

mysql> GRANT ALL PRIVILEGES ON ambari.* TO 'ambari'@'localhost' IDENTIFIED BY 'ambari_123';

Query OK, 0 rows affected, 1 warning (0.03 sec)

mysql> GRANT ALL PRIVILEGES ON ambari.* TO 'ambari'@'%' IDENTIFIED BY 'ambari_123';

Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> FLUSH PRIVILEGES;

Query OK, 0 rows affected (0.00 sec)
```



- 使用ambari登录

```sql
[root@hadoop117 ~] mysql -ambari -ambari_123

Enter password: 

Welcome to the MySQL monitor.  Commands end with ; or \g.

Your MySQL connection id is 4

Server version: 5.7.26 MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its

affiliates. Other names may be trademarks of their respective

owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.



mysql> show databases;

+--------------------+

| Database           |

+--------------------+

| information_schema |

| ambari             |

+--------------------+

2 rows in set (0.03 sec)



mysql> use ambari;

Database changed

mysql> source /var/lib/ambari-server/resources/Ambari-DDL-MySQL-CREATE.sql;

\# 执行完，查看有无报错信息，并查看数据表

mysql> show tables;

+-------------------------------+

| Tables_in_ambari              |

+-------------------------------+

| ClusterHostMapping            |

| QRTZ_BLOB_TRIGGERS            |

| QRTZ_CALENDARS                |

| QRTZ_CRON_TRIGGERS            |

| QRTZ_FIRED_TRIGGERS           |

| QRTZ_JOB_DETAILS              |

| QRTZ_LOCKS                    |

| QRTZ_PAUSED_TRIGGER_GRPS      |

| QRTZ_SCHEDULER_STATE          |

| QRTZ_SIMPLE_TRIGGERS          |

| QRTZ_SIMPROP_TRIGGERS         |

| QRTZ_TRIGGERS                 |

| adminpermission               |

| adminprincipal                |

| adminprincipaltype            |

| adminprivilege                |

| adminresource                 |

| adminresourcetype             |

| alert_current                 |

| alert_definition              |

| alert_group                   |

| alert_group_target            |

| alert_grouping                |

| alert_history                 |

| alert_notice                  |

| alert_target                  |

| alert_target_states           |

| ambari_configuration          |

| ambari_operation_history      |

| ambari_sequences              |

| artifact                      |

| blueprint                     |

| blueprint_configuration       |

| blueprint_setting             |

| clusterconfig                 |

| clusters                      |

| clusterservices               |

| clusterstate                  |

| confgroupclusterconfigmapping |

| configgroup                   |

| configgrouphostmapping        |

| execution_command             |

| extension                     |

| extensionlink                 |

| groups                        |

| host_role_command             |

| host_version                  |

| hostcomponentdesiredstate     |

| hostcomponentstate            |

| hostconfigmapping             |

| hostgroup                     |

| hostgroup_component           |

| hostgroup_configuration       |

| hosts                         |

| hoststate                     |

| kerberos_descriptor           |

| kerberos_keytab               |

| kerberos_keytab_principal     |

| kerberos_principal            |

| key_value_store               |

| kkp_mapping_service           |

| members                       |

| metainfo                      |

| permission_roleauthorization  |

| remoteambaricluster           |

| remoteambariclusterservice    |

| repo_applicable_services      |

| repo_definition               |

| repo_os                       |

| repo_tags                     |

| repo_version                  |

| request                       |

| requestoperationlevel         |

| requestresourcefilter         |

| requestschedule               |

| requestschedulebatchrequest   |

| role_success_criteria         |

| roleauthorization             |

| servicecomponent_version      |

| servicecomponentdesiredstate  |

| serviceconfig                 |

| serviceconfighosts            |

| serviceconfigmapping          |

| servicedesiredstate           |

| setting                       |

| stack                         |

| stage                         |

| topology_host_info            |

| topology_host_request         |

| topology_host_task            |

| topology_hostgroup            |

| topology_logical_request      |

| topology_logical_task         |

| topology_request              |

| upgrade                       |

| upgrade_group                 |

| upgrade_history               |

| upgrade_item                  |

| user_authentication           |

| users                         |

| viewentity                    |

| viewinstance                  |

| viewinstancedata              |

| viewinstanceproperty          |

| viewmain                      |

| viewparameter                 |

| viewresource                  |

| viewurl                       |

| widget                        |

| widget_layout                 |

| widget_layout_user_widget     |

+-------------------------------+

111 rows in set (0.00 sec)
```



## 5.2 启动Ambari-Server

- 如果启动失败，关闭服务【ambari-server stop】，重新启动

```shell
[root@hadoop117 ~] ambari-server start

Using python  /usr/bin/python

Starting ambari-server

Ambari Server running with administrator privileges.

Organizing resource files at /var/lib/ambari-server/resources...

Ambari database consistency check started...

Server PID at: /var/run/ambari-server/ambari-server.pid

Server out at: /var/log/ambari-server/ambari-server.out

Server log at: /var/log/ambari-server/ambari-server.log

Waiting for server start......................................................

Server started listening on 8080



DB configs consistency check: no errors and warnings were found.

Ambari Server 'start' completed successfully.
```



## 5.3 安装Agent

- hadoop117-hadoop121 所有节点安装ambari-agent

yum -y install ambari-agent



## 5.4 访问Ambari web页面

- 默认端口8080，Username：admin；Password：admin；http://hadoop117:8080

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MWZkNjI1ODkzYWJiZjlhYWMxYjg1YzU4MDZjYjA0MDBfSzB4Z0NEZGFKUTd1SERMM2J4R1YyUThCd2VBVTE1SFlfVG9rZW46Ym94Y24zTWNaTGFsdEcwcm5qaHVuUEIwVTBnXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MTgyOTg2ZGJjN2VhYWI1YmM4ODY5ZWExM2ExYTI2NDRfUks3N05ITDAzQ1FPTE1uWEZ3MGI4NnFmVlVzN2l5TU5fVG9rZW46Ym94Y242OFpMcXM0TzI4TGtUOU16UFN0MDdjXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

# 6. 安装集群

## 6.1 集群与服务安装

- 点击启动安装向导创建集群
- 配置集群名称
- 选择版本

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=ZDQ4MjZhNzAxYWE1YWMwNGNmNjYxNzZkNzEzMDUwNDBfZXo3SUhueHN2alViVHZUdkVabGFzSjJtMkpuR2p4WWNfVG9rZW46Ym94Y254NE5YSHlqbXp3aGRrNk5nRjdDalFiXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 配置节点密钥（配置免密登录时的密钥）



![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=ZjlhNmYyMzM4ZjNlYTY5MjZlODFmZjFlNzA0ODQzYzFfR3M0R2tYbFIyN0FtUEdIRGxWQVFXRDVDbGdBUllnRzNfVG9rZW46Ym94Y25qdnRWRjMwanFvTzFLYVlRRUpiZ2NnXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 主机确认
- 选择组件

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MTVjZTExMDFlMjU3NTA1NGJkNTdhMDM1MGNjNzBiYzlfZjFxc0JkNVRRWFBaMUtFazRnWHFVcnRONllsTVdvS2NfVG9rZW46Ym94Y25pOW1MZ0ZjejF0OVFvNWdMNkhoeHBlXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 分配从属和客户端
- 定制服务（设置账号密码，数据库）

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=YzcxZmEwNTJiYzE2NWQ4M2Q1M2Y3YWFkOTJlMTJhZWVfY1JidWlPRFFFMnVvNHlZR25aa2g1bTBHbzNKRUN1OW9fVG9rZW46Ym94Y254a3MyeFo1cVB4UmprRzFpMTE5NkxmXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

```shell
[root@hadoop117 ~] ambari-server setup --jdbc-db=mysql --jdbc-driver=/usr/share/java/mysql-connector-java.jar
```



- 集群整体概况，点击部署
- 完成

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MjRhODUyOGFmZDFlMjc1MTAzMTRjMTJmMjk5NGFkNjRfVzdGbkVMcUtZRkI5VlNua01QY3ZVc1RWeDVsQUNPQ3pfVG9rZW46Ym94Y253N3lnODFYdTl1M3p2RjVnYlBUQlVmXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



## 6.2 Flume安装

上传压缩包到hadoop119，hadoop120

- 解压

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=YjU1MDU2YTk5NTFmMjhlNDhkOGQ4ZmU1NmU0ODAzYjdfRTRZeldreXBPaGdMeEpBQVR6UGpQb21SdHVEYmhLYXJfVG9rZW46Ym94Y25HRzRkRTdGQjd2dklSS2tHVmxDTHNmXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

- 修改配置文件 conf/flume-env.sh 添加JAVA_HOME

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=NDExMzcyNmVmZjQ2MGIxOGI3OWViY2I0MGVkZmYzNWNfaGZOT3BFWXRKOU1ieEtnRnRmVzVtS3lMVVY2ZTVXVGVfVG9rZW46Ym94Y25kSWhyV0RkQTA3b2dya0lGcmlHVWlkXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

## 6.3 Flink安装

- 去官网下载安装包：flink-1.9.2-bin-scala_2.12.tgz，flink-shaded-hadoop-2-uber-2.7.5-10.0.jar，jersey-client-1.9.jar，jersey-core-1.9.jar，jersey-server-1.9.jar
- 将flink-1.9.2-bin-scala_2.12.tgz解压到/opt/module/flink-1.9.2
- 将flink-shaded-hadoop-2-uber-2.7.5-10.0.jar，jersey-client-1.9.jar，jersey-core-1.9.jar，jersey-server-1.9.jar复制到/opt/module/flink-1.9.2/lib



## 6.4 数据可视化框架安装

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=MjYyY2M2ZTZkZDc1ZGM5OTBiYmIyZDQ0MmExN2ZlZjFfOEdacTVEbnk2OFFhTDk0bVo1b3pEdEJ5RFNpYmYwNDdfVG9rZW46Ym94Y25xaHppTnVDdnh2MWRaYktNTUdlbUg5XzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)



## 6.5 版本信息

![img](https://baas.feishu.cn/space/api/box/stream/download/asynccode/?code=Mjg4NWY0N2QxMjZhZDllMzYzZmVmMTUyOTM1MzJjNjVfa2tqR1Z2bmJtU1N3bDlob2p6QXc3U2h2NkJySXVlV2dfVG9rZW46Ym94Y25mT2ViOVQ0Sk5BaWMxaFJIN3NWZXNlXzE2NTAzNjA0OTU6MTY1MDM2NDA5NV9WNA)

## 