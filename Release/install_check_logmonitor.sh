#!/bin/sh

user=`whoami`
if [ ${user} != "root" ]
then
    echo
    echo "Error: Execution user must be root!"
    echo
    exit 1
fi

CUR=`dirname ${0}`
LOGMONITOR_VERSION=`cat "${CUR}/version.txt"`

echo "##########################################################"
echo "  1. Docker Installation Check"
echo "##########################################################"
docker --version
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : Docker is not installed."
    exit 1
fi

echo "##########################################################"
echo "  2. Docker Image Version Check"
echo "##########################################################"

if [ -n "$LOGMONITOR_VERSION" ]; then
  echo LOGMONITOR_VERSION: ${LOGMONITOR_VERSION}
else
  echo "ERROR : Version in not exist!!"
  exit 1
fi

# RSS-Proxy
RET=$(docker container ls --filter "name=RSS-Proxy" --format "{{.Image}}")
echo Image: ${RET}

if [ -n "$RET" ]; then
  if [[ "$RET" =~ "${LOGMONITOR_VERSION}" ]]; then
    echo "RSS-Proxy:${LOGMONITOR_VERSION} is installed"
  else
    echo "ERROR : The version of RSS-Proxy container is not" ${LOGMONITOR_VERSION}
    exit 1
  fi
else
  echo "ERROR : There is no RSS-Proxy container!!"
  exit 1
fi

# Log-Monitor-Server
RET=$(docker container ls --filter "name=Log-Monitor-Server" --format "{{.Image}}")
echo Image: ${RET}

if [ -n "$RET" ]; then
  if [[ "$RET" =~ "${LOGMONITOR_VERSION}" ]]; then
    echo "Log-Monitor-Server:${LOGMONITOR_VERSION} is installed"
  else
    echo "ERROR : The version of Log-Monitor-Server is not" ${LOGMONITOR_VERSION}
    exit 1
  fi
else
  echo "ERROR : There is no Log-Monitor-Server Container!!"
  exit 1
fi

# Log-Monitor-Database
RET=$(docker container ls --filter "name=Log-Monitor-Database" --format "{{.Image}}")
echo Image: ${RET}
if [ -n "$RET" ]; then
  if [[ "$RET" =~ "13-alpine" ]]; then
    echo "Log-Monitor-Database:13-alpine is installed"
  else
    echo "ERROR : The version of Log-Monitor-Database is not 13-alpine"
    exit 1
  fi
else
  echo "ERROR : There is no Log-Monitor-Database Container"
  exit 1
fi

echo "##########################################################"
echo "  3. Directory Existence Check"
echo "##########################################################"

test -d /CANON/LOGMONITOR
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR is not installed."
    exit 1
fi

test -d /CANON/CRAS/APP
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/CRAS/APP is not installed."
    exit 1
fi

test -d /CANON/LOGMONITOR/FILES/downloads
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR/FILES/downloads is not installed."
    exit 1
fi

test -d /CANON/LOGMONITOR/FILES/uploads
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR/FILES/uploads is not installed."
    exit 1
fi

test -d /CANON/LOGMONITOR/DB
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR/DB is not installed."
    exit 1
fi

test -d /CANON/LOGMONITOR/DEVLOG/tomcat
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR/DEVLOG/tomcat is not installed."
    exit 1
fi

test -d /CANON/LOGMONITOR/DEVLOG/httpd
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : /CANON/LOGMONITOR/DEVLOG/httpd is not installed."
    exit 1
fi

echo "All directories exist."

echo "##########################################################"
echo "  4. Running Container Check"
echo "##########################################################"
docker container ls --format="{{.Names}}" | grep RSS-Proxy
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : Container RSS-Proxy is not running."
    exit 1
fi

docker container ls --format="{{.Names}}" | grep Log-Monitor-Server
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : Container Log-Monitor-Server is not running."
    exit 1
fi

docker container ls --format="{{.Names}}" | grep Log-Monitor-Database
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "ERROR : Container Log-Monitor-Database is not running."
    exit 1
fi

echo "##########################################################"
echo "  Installation Status : OK"
echo "##########################################################"
