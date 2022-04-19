python3 /flumeParser.py
cd /opt/flume-1.9.0
bash bin/flume-ng agent -n agent -c conf -f conf/flume-conf.properties \
  -Dflume.monitoring.type=http -Dflume.monitoring.port=5454
