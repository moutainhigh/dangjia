FROM daocloud.io/library/java:openjdk-8-jre-alpine
MAINTAINER qiyuxiang<qiyuxiangjava@qq.com>

VOLUME /tmp
COPY ./target/dangjia-eureka-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]

EXPOSE 8888
