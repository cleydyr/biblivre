FROM cleydyr/tomcat:7-jdk8
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"

ENTRYPOINT catalina.sh run

EXPOSE 8080
EXPOSE 8000