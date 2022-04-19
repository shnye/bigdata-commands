docker run -d -i -p 13000:3000 \
--name grafana \
-v "/etc/localtime:/etc/localtime" \
-e "GF_SERVER_ROOT_URL=http://grafana.server.name" \
-e "GF_SECURITY_ADMIN_PASSWORD=admin8888" \
-v /data/grafana/storage:/var/lib/grafana \
grafana/grafana
