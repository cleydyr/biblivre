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
COPY target/Biblivre6/ ${CATALINA_HOME}/webapps/Biblivre6/
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
EXPOSE 8080