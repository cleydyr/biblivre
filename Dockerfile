FROM tomcat:9-jre8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" >> /etc/apt/sources.list.d/pgdg.list
RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
RUN apt-get update
RUN apt-get install -y maven postgresql-11 default-jdk
ENV GITHUB_USER cleydyr
ENV BRANCH_NAME master
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
ENV POSTGRES_HOME="/etc/postgresql/11/main"
ENTRYPOINT echo "listen_addresses = '*'" >> ${POSTGRES_HOME}/postgresql.conf \
&& 	echo "host    all             all              0.0.0.0/0                       md5" >> ${POSTGRES_HOME}/pg_hba.conf \
&& 	/etc/init.d/postgresql start \
&&	wget https://github.com/$GITHUB_USER/biblivre/archive/$BRANCH_NAME.zip \
&&	unzip $BRANCH_NAME.zip -d /tmp && rm $BRANCH_NAME.zip \
&&	cd /tmp/biblivre-$BRANCH_NAME/lib/ \
&&	sh maven_deps.sh \
&&	cd .. \
&&	mvn sass:update-stylesheets package -Ddebug=true \
&&	su postgres -c "psql -U postgres -f sql/createdatabase.sql" \
&&	su postgres -c "psql -U postgres -f sql/biblivre4.sql -d biblivre4" \
&&  cp target/Biblivre6.war $CATALINA_HOME/webapps \
&&	catalina.sh run

EXPOSE 8080
