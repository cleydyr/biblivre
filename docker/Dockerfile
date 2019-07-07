FROM tomcat:9-jdk8
RUN apt-get update
RUN apt-get install -y postgresql-client
COPY ./deploy.sh ${CATALINA_HOME}/deploy.sh
COPY ./postgresql-42.2.6.jar ${CATALINA_HOME}/lib
RUN chmod a+x /usr/local/tomcat/deploy.sh
ENTRYPOINT ./deploy.sh
EXPOSE 8080