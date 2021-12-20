# 도커 이미지 생성에 대하여

## RSS 시스템 도커 구성

1. Proxy
* Base : httpd
* 추가 내용 : 없음 디렉토리 지정하고 그냥 굴리면 됨.

2. Database
* Base : postgres
* 추가 내용 : 없음

3. Rapid Collector
* Base : tomcat
* 추가 내용 : 
    * rssportal.war
    * fsm.war

4. Service Manager
* Base : centos:7
* 추가 내용 :
    * java-11-openjdk
    * docker-ce-cli
    * tomcat
    * servicemanager.war 

5. FileServiceCollect

```
docker build --no-cache -t sm_base -f ServiceManagerBase .
```