FROM java:8-alpine
MAINTAINER mufeng 
RUN apk add bash jq python3 --no-cache
RUN mkdir -p /opt

ENV FLUME_HOME=/opt/flume-1.9.0
ENV PATH=$PATH:$FLUME_HOME/bin
ENV CONFIG="{}"
COPY /flume-1.9.0 /opt/flume-1.9.0
COPY /hdfs_jars/* /opt/flume-1.9.0/lib/
COPY /lib/* /opt/flume-1.9.0/lib/
COPY /flumeParser.py /
COPY /start.sh /
COPY /default.json /
#EXPOSE 80
RUN chmod 700 /start.sh \
    && ln -s /opt/flume-1.9.0/conf/flume-conf.properties /flume.conf \
    && ln -s /opt/flume-1.9.0/logs/flume.log /flume.log 
ENTRYPOINT /./start.sh
#CMD ["bin/flume-ng","agent","-n","agent","-c","conf","-f","conf/flume-conf.properties"]
