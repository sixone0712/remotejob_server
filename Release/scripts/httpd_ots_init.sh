#!/bin/sh

CUR=`dirname ${0}`

echo
echo START: httpd_ots_init.sh
echo

\cp -f "${CUR}/../data/httpd.conf" /etc/httpd/conf/httpd.conf
\cp -f "${CUR}/../data/fs-accctrl.conf" /etc/httpd/conf.d/fs-accctrl.conf

\cp -f "${CUR}/../data/iptables" /etc/sysconfig/iptables
chown root:root /etc/sysconfig/iptables
chmod 644 /etc/sysconfig/iptables

echo
echo END: httpd_ots_init.sh
echo
