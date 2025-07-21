FROM tomcat:9.0.107-jdk17-temurin

#기존 webapps 폴더 제거
RUN rm -rf /usr/local/tomcat/webapps/*

# WAR 파일 복사
COPY build/libs/refit-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080