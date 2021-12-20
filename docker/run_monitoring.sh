
mkdir -p /LOG/CANON

readlink -s /CANON
RET=$?
if [ ${RET} -ne 0 ]
then
    ln -s /LOG/CANON /CANON
fi

mkdir -p /CANON/LOGMONITOR /CANON/CRAS/APP /CANON/LOGMONITOR/DEVLOG /CANON/LOGMONITOR/FILES/downloads /CANON/LOGMONITOR/FILES/uploads /CANON/LOGMONITOR/DB /CANON/LOGMONITOR/DEVLOG/tomcat /CANON/LOGMONITOR/DEVLOG/httpd

### Copy CRAS Source File ###
cp -f cras.tar /CANON/CRAS/APP

docker network create rss

# Start Log-Monitor-Database
docker run -d \
--name Log-Monitor-Database \
--network rss \
--restart always \
--mount type=bind,source=/CANON,target=/CANON \
--mount type=bind,source=/CANON/LOGMONITOR/DB,target=/var/lib/postgresql/data \
-e POSTGRES_USER=rssadmin \
-e POSTGRES_PASSWORD=1234 \
-e POSTGRES_DB=logdb \
-e TZ=`timedatectl | grep "Time zone" | awk '{printf $3}'` \
-p 5432:5432 \
10.1.31.230/logmonitor/postgres:13-alpine

# Start Log-Monitor-Server
docker run -d \
--name Log-Monitor-Server \
--network rss \
--restart always \
--tmpfs /tmp \
--tmpfs /run \
--mount type=bind,source=/CANON,target=/CANON \
-e SPRING_PROFILES_ACTIVE=release \
-e TZ=`timedatectl | grep "Time zone" | awk '{printf $3}'` \
--cap-add=SYS_PTRACE \
10.1.31.230/logmonitor/log-monitor-server:latest


# Start Log-Monitor-Proxy
docker run -d \
--name Log-Monitor-Proxy \
--network rss \
--restart always \
--mount type=bind,source=/CANON,target=/CANON \
-e TZ=`timedatectl | grep "Time zone" | awk '{printf $3}'` \
-p 80:80 \
10.1.31.230/logmonitor/log-monitor-proxy:latest

