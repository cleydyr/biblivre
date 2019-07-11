FROM tomcat:9-jdk8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" >> /etc/apt/sources.list.d/pgdg.list
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
RUN apt-get update
RUN apt-get install -y postgresql-client-11 && rm -rf /var/cache/apk/*
RUN wget --quiet -O ${CATALINA_HOME}/lib/postgresql-42.2.6.jar https://jdbc.postgresql.org/download/postgresql-42.2.6.jar 
COPY ./scripts/deploy.sh ${CATALINA_HOME}/deploy.sh
COPY ./target/Biblivre4 ${CATALINA_HOME}/webapps/Biblivre4
RUN chmod a+x /usr/local/tomcat/deploy.sh
ENTRYPOINT ./deploy.sh
EXPOSE 8080