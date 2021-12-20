# Getting Started with Spring Boot

This project was bootstrapped with [spring initializr](https://start.spring.io/).


## Development Run
1. local에 postgres가 설치되어 있어야한다.
   - username: rssuser
   - password: rssuser 
   - url: jdbc:postgresql://localhost:5432/logdb  
    ※ 없는 경우, 하기의 명령어로 Docker로 실행
        ```
        \docker\postgres\docker-compose up -d
        ```

2. build_front.bat 실행하여 frontend 소스 빌드
3. run application  
   ※ active profile을 dev로 설정해야 정상적으로 실행됨.

## Release
1. build.gradle에서 version 변경  
    ```
    allprojects {
        version '1.0.0'  ← 버전 변경   
    }
    ```  
2. docker image 준비  
하기의 docker images가 local에 pull되어 있어야 한다. 없으면 pull하여 이미지 다운로드
   - postgres:13-alpine
   - httpd:2.4.446 
   - centos:7
    
3. release 실행  
    - setup_Log_Monitor_Ver_xx_xx_xx.zip 파일이 생성

    
   