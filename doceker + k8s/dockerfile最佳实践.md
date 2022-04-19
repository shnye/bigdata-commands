# dockerfile最佳实践

- ​	把最频繁修改的放在dockerfile的最后的部分，以免破坏build的时候的缓存，同样复制文件时。在允许的情况下，尽量复制具体文件不要指定文件夹，否则也会破坏build缓存。
- ​	同样如果需要进行一系列的apt-get yum等安装删除包的操作的时候，尽量不要分步进行用 \ 去连接多个操作（把相关的缓存到一起）。
- ​	安装依赖时可以使用--no-install-recommends 排除掉一些你并没有真正使用过的依赖。
- ​	安装完以后 使用 && rm -rf /var/lib/apt/lists/* 去删除安装包 	
- ​	尽可能使用官方的镜像以及明确的版本号作为基底。
- ​	如果只需要部分的内容到下一步进行使用，可以使用多步骤进行构建 例如：

```dockerfile
#如果只需要部分的内容到下一步进行使用，可以使用多步骤进行构建 
#这样，就可以使用不同的构建镜像对需要的进行构建以后再最后的执行镜像进行使用

FROM maven:3.6-jdk-8-alpine As builder
WORKDIR /app 
COPY pom. xml .
RUN mvn -e -B dependency:resolve 
COPY src ./src
RUN mvn -e -B package

FROM openjdk:8-jre-alpine As runner
COPY --from=builder /app/target/app.jar /
CMD ["java", "-jar", "/app.jar"]

#通过--target stage_name 来控制具体build哪个阶段的镜像
$ docker build --target runner .


```

​	通过参数进行控制	

```dockerfile
#通过参数进行控制
ARG flavor=alpine
FROM maven:3.6-jdk-8-alpine AS builder
......
FROM openjdk:8-jre-Şflavor AS release 
COPY --from=builder /app/target/app.jar / 
CMD ["java", "-jar", "/app. jar"]

$ docker build --target release --build-arg flavor=xxxxx .
```



包缓存挂载方式

```dockerfile
#包缓存挂载方式 Context mounts (v18.09+ w/ BuildKit)
# syntax=docker/dockerfile:1-experimental
FROM maven:3. 6-jdk-8-alpine AS builder 
WORKDIR /app
RUN --mount=target=. --mount=type=cache, target=/root/.m2 \
	&& mvn package -DoutputDirectory=/
	
FROM openjdk:8-jre-alpine 
COPY --from=builder /app.jar / 
CMD ["java","/app. jar"]
```



secrets

```dockerfile
# syntax=docker/dockerfile:1-experimental 
FROM baseimage
RUN ....
RUN --mount=type=secret,id=aws, target=/root/.aws/credentials ,required ./fetch-assets-from-s3. sh 
RUN ./build-scripts. sh
$ docker build --secret id=aws,src=~/.aws/credentials .
```



Private git repos (DO THIS, v18.09+ w/ BuildKit)

```dockerfile
#Private git repos (DO THIS, v18.09+ w/ BuildKit)
FROM alpine
RUN apk add --no-cache openssh-client
RUN mkdir -p -m 0700 «/. ssh && ssh-keyscan github. com >> «/.ssh/known hosts
ARG REPO REF=19ba7bcd9976ef8a9bd086187df19ba7bcd997f2 
RUN --mount=type=ssh,required \
	git clone gitegithub.com:org/repo /work && cd /work \
	&& git checkout -b ŞREPO REF
	
$ eval $(ssh-agent)
$ ssh-add "/.ssh/id rsa
$ docker build --ssh=default
```

