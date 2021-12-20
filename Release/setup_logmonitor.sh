#!/bin/sh

echo
echo "START: setup_logmonitor.sh"
echo

CUR=`dirname ${0}`

user=`whoami`
if [ ${user} != "root" ]
then
    echo
    echo "Error: Execution user must be root!"
    echo
    exit 1
fi

CUR=`dirname ${0}`

test -f "${CUR}/version.txt"
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "version.txt is not exists..."
    echo "Installation Failed."
    exit 1
fi

LOGMONITOR_VERSION=`cat "${CUR}/version.txt"`
echo "##########################################################"
echo "## Install Log Monitor $LOGMONITOR_VERSION ##"
echo "##########################################################"

echo "##########################################################"
echo "  CHECK EXISTING SYSTEM"
echo "##########################################################"
docker --version
RET=$?
if [ ${RET} -ne 0 ]; then
    echo "Docker System is not installed. Proceed with initial system installation."
else
    echo "Docker System installed. Proceed with only docker image update."
    sh ${CUR}/setup_update.sh ESP
    exit 1
fi

echo "##########################################################"
echo "  INSTALL OR RESTART DOCKER"
echo "##########################################################"
docker --version
RET=$?
if [ ${RET} -ne 0 ]; then
    echo "Docker is not installed. Install Docker first."
    sh "${CUR}/install_docker.sh"
else
  systemctl restart docker
fi

#only 2021-phase4 : delete logmonitor folder(all)
rm -rf /LOG/CANON/LOGMONITOR

echo "##########################################################"
echo "  MAKE DIRECTORIES."
echo "##########################################################"
rm -rf /CANON/LOGMONITOR

mkdir -p /LOG/CANON

readlink -s /CANON
RET=$?
if [ ${RET} -ne 0 ]
then
    ln -s /LOG/CANON /CANON
fi

mkdir -p /CANON/LOGMONITOR /CANON/CRAS/APP /CANON/LOGMONITOR/DEVLOG /CANON/LOGMONITOR/FILES/downloads /CANON/LOGMONITOR/FILES/uploads /CANON/LOGMONITOR/DB /CANON/LOGMONITOR/DEVLOG/tomcat /CANON/LOGMONITOR/DEVLOG/httpd

### Docker Image Load ###
# Docker Image Load
echo "##########################################################"
echo "  LOAD DOCKER IMAGES FROM ARCHIVE."
echo "##########################################################"
docker image load -i "${CUR}/logmonitor_images_${LOGMONITOR_VERSION}.tar.gz"


echo "##########################################################"
echo "  Start Containers"
echo "##########################################################"
# Start Containers
sh ${CUR}/run_logmonitor.sh ${LOGMONITOR_VERSION}
# Prune unused image.
# docker image prune -a -f
# Prune dangling image.
docker image prune -f

echo "##########################################################"
echo "  INSTALL SUCCESS!!!"
echo "##########################################################"

echo
echo "END: setup_monitoring.sh"
echo