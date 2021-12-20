CUR=`dirname ${0}`


if [ $# -eq 0 ]; then
  test -f "${CUR}/version.txt"
  RET=$?
  if [ ${RET} -eq 0 ];
  then
    LOGMONITOR_VERSION=`cat "${CUR}/version.txt"`
  else
    LOGMONITOR_VERSION="latest"
  fi
elif [ $# -eq 1 ]; then
  LOGMONITOR_VERSION=$1
else
  echo "invalid argument count"
  exit 1
fi

test -f "${CUR}/logmonitor_port.txt"
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "logmonitor_port.text is not exists..."
    echo "Install default port 80."
    LOGMONITOR_PORT=80
else
  LOGMONITOR_PORT=`cat "${CUR}/logmonitor_port.txt"`
  echo "Install port ${LOGMONITOR_PORT}."
fi

docker network ls --format "{{.Name}}" | grep rss
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "Create Docker Network rss."
    docker network create rss
    RET=$?
    if [ ${RET} -ne 0 ]
    then
        echo "\"docker network create rss\" failed."
    fi
fi

# Start Log-Monitor-Database
docker container ls -a --format="{{.Names}}" | grep Log-Monitor-Database
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "Stop Existing Container Log-Monitor-Database."
    docker container stop Log-Monitor-Database && docker container rm Log-Monitor-Database
fi
echo "Run Container Log-Monitor-Database."
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
-p 5442:5432 \
postgres:13-alpine

sleep 10s

# Start Log-Monitor-Server
docker container ls -a --format="{{.Names}}" | grep Log-Monitor-Server
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "Stop Existing Container Log-Monitor-Server."
    docker container stop Log-Monitor-Server && docker container rm Log-Monitor-Server
fi
echo "Run Container Log-Monitor-Server."
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
log-monitor-server:${LOGMONITOR_VERSION}

# Start RSS-Proxy
# Legacy Log-Monitor-Proxy
docker container ls -a --format="{{.Names}}" | grep Log-Monitor-Proxy
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "Stop Existing Container Log-Monitor-Proxy(httpd)."
    docker container stop Log-Monitor-Proxy && docker container rm Log-Monitor-Proxy
fi
# Legacy RSS-Proxy
docker container ls -a --format="{{.Names}}" | grep RSS-Proxy
RET=$?
if [ ${RET} -eq 0 ]
then
    echo "Stop Existing Container RSS-Proxy(httpd)."
    docker container stop RSS-Proxy && docker container rm RSS-Proxy
fi
echo "Run Container RSSProxy(httpd)."
docker run -d \
--name RSS-Proxy \
--network rss \
--restart always \
--mount type=bind,source=/CANON,target=/CANON \
-e TZ=`timedatectl | grep "Time zone" | awk '{printf $3}'` \
--add-host=hostsystem:`docker network inspect rss | grep "Gateway" | awk '{print $2}' | sed 's/\"//g'` \
-p ${LOGMONITOR_PORT}:80 \
rss-proxy:${LOGMONITOR_VERSION}


