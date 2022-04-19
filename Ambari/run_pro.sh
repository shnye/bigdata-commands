docker run -itd --name prometheus -p 9090:9090 \
-v ./prometheus.yml:/etc/prometheus/prometheus.yml \
-v /etc/localtime:/etc/localtime:ro \
prom/prometheus
