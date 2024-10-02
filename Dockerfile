FROM tomcat:10-jdk21
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN apt-get update \
	&& apt install -y postgresql-common \
	&& /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh -y \
	&& apt install -y \
	postgresql-client-16 \
	&& rm -rf /var/cache/apk/*
RUN rm -rf "${CATALINA_HOME}/webapps/ROOT"
COPY target/Biblivre6/WEB-INF/tags ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tags
COPY target/Biblivre6/WEB-INF/templates ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/templates
COPY target/Biblivre6/WEB-INF/tlds ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tlds
COPY target/Biblivre6/WEB-INF/lib ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/lib
COPY target/Biblivre6/WEB-INF/jsp ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/jsp
COPY target/Biblivre6/WEB-INF/classes ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/classes
COPY target/Biblivre6/WEB-INF/web.xml ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/web.xml
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
EXPOSE 8080