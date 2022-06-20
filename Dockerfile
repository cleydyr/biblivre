FROM tomcat:9-jdk17
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ bullseye-pgdg main" >> /etc/apt/sources.list.d/pgdg.list
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN wget --quiet -O - "https://www.postgresql.org/media/keys/ACCC4CF8.asc" | apt-key add -
RUN apt-get update \
	&& apt-get install -y netcat postgresql-client-11 \
	&& apt-get clean \
	&& rm -rf /var/cache/apk/*
RUN rm -rf ${CATALINA_HOME}/webapps/ROOT
COPY scripts/wait-for.sh /usr/local/bin/wait-for.sh
RUN chmod +x /usr/local/bin/wait-for.sh
COPY target/Biblivre6/ ${CATALINA_HOME}/webapps/Biblivre6/

EXPOSE 8080