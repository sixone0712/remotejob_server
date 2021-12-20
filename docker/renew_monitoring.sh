CUR=`dirname ${0}`

docker pull 10.1.31.230/logmonitor/postgres:13-alpine
docker pull 10.1.31.230/logmonitor/log-monitor-server:latest
docker pull 10.1.31.230/logmonitor/log-monitor-proxy:latest

#docker tag 10.1.31.230/logmonitor/postgres:13-alpine postgres:13-alpine
#docker tag 10.1.31.230/logmonitor/log-monitor-proxy:latest log-monitor-proxy:latest
#docker tag 10.1.31.230/logmonitor/log-monitor-server:latest log-monitor-server:latest
#
#sh "${CUR}/stop_esp.sh"
#sh "${CUR}/run_esp.sh"
#
#docker image prune --all --force
#docker volume prune --force
