FROM tomcat:10-jdk21
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN apt-get update \
	&& apt-get install -y \
	netcat \
	gnupg \
	&& wget -q -O- "https://www.postgresql.org/media/keys/ACCC4CF8.asc" | apt-key add - \
	&& sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt jammy-pgdg main" > /etc/apt/sources.list.d/pgdg.list' \
	&& apt update \
	&& apt install -y \
	postgresql-client-11 \
	&& rm -rf /var/cache/apk/*
RUN rm -rf "${CATALINA_HOME}/webapps/ROOT"
COPY scripts/wait-for.sh /usr/local/bin/wait-for.sh
RUN chmod +x /usr/local/bin/wait-for.sh
COPY target/Biblivre6/WEB-INF/tags ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tags
COPY target/Biblivre6/WEB-INF/templates ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/templates
COPY target/Biblivre6/WEB-INF/tlds ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tlds
COPY target/Biblivre6/WEB-INF/lib ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/lib
COPY target/Biblivre6/WEB-INF/jsp ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/jsp
COPY target/Biblivre6/WEB-INF/classes ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/classes
COPY target/Biblivre6/WEB-INF/web.xml ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/web.xml
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
EXPOSE 8080