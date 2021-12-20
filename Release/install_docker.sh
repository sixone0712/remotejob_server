#!/bin/sh
CUR=`dirname ${0}`

echo "##########################################################"
echo "  Install Docker 19.03"
echo "##########################################################"

yum localinstall -y "${CUR}/container-selinux-2.119.2-1.911c772.el7_8.noarch.rpm" "${CUR}/containerd.io-1.3.7-3.1.el7.x86_64.rpm" "${CUR}/docker-ce-cli-19.03.13-3.el7.x86_64.rpm" "${CUR}/docker-ce-19.03.13-3.el7.x86_64.rpm"
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "Docker installation failed."
    exit ${RET}
fi

systemctl enable docker
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "enabling docker service failed."
    exit ${RET}
fi

systemctl start docker
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "starting docker service failed."
    exit ${RET}
fi

docker network create rss
RET=$?
if [ ${RET} -ne 0 ]
then
    echo "starting docker service failed."
fi

echo "##########################################################"
echo "  Docker installation success."
echo "##########################################################"


