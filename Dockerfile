FROM tomcat:11-jdk25
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN apt-get update \
	&& apt-get install -y --no-install-recommends postgresql-common \
	&& /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh -y \
	&& apt-get install -y --no-install-recommends \
	postgresql-client-16 \
	&& apt-get clean \
	&& rm -rf /var/lib/apt/lists/*
RUN rm -rf "${CATALINA_HOME}/webapps/ROOT"
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
# Stable paths first so a Java rebuild reuses the lib layer.
COPY target/Biblivre6/WEB-INF/web.xml ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/web.xml
COPY target/Biblivre6/WEB-INF/tlds ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tlds
COPY target/Biblivre6/WEB-INF/lib ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/lib
COPY target/Biblivre6/WEB-INF/tags ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/tags
COPY target/Biblivre6/WEB-INF/templates ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/templates
COPY target/Biblivre6/WEB-INF/jsp ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/jsp
COPY target/Biblivre6/WEB-INF/classes ${CATALINA_HOME}/webapps/Biblivre6/WEB-INF/classes
EXPOSE 8080
