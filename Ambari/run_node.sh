docker run -d -p 9100:9100 \
--name prom/node-exporter \
-v "/proc:/host/proc" \
-v "/sys:/host/sys" \
-v "/:/rootfs" \
-v "/etc/localtime:/etc/localtime" \
--net=devNet \
prom/node-exporter \
--path.procfs /host/proc \
--path.sysfs /host/sys \
--collector.filesystem.ignored-mount-points "^/(sys|proc|dev|host|etc)($|/)"
