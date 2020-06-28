FROM cleydyr/tomcat:7-jdk8
RUN apt-get update
RUN apt-get install -y maven postgresql
ENV GITHUB_USER cleydyr
ENV BRANCH_NAME 5.x
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
ENV REPO_NAME biblivre
ENTRYPOINT echo "listen_addresses = '*'" >> /etc/postgresql/9.6/main/postgresql.conf \
&& 	echo "host    all             all              0.0.0.0/0                       md5" >> /etc/postgresql/9.6/main/pg_hba.conf \
&& 	/etc/init.d/postgresql start \
&&	wget https://github.com/$GITHUB_USER/$REPO_NAME/archive/$BRANCH_NAME.zip \
&&	unzip $BRANCH_NAME.zip -d /tmp && rm $BRANCH_NAME.zip \
&&	cd /tmp/$REPO_NAME-$BRANCH_NAME/lib/ \
&&	cd .. \
&&	mvn package -Ddebug=true \
&&	su postgres -c "psql -U postgres -f sql/createdatabase.sql" \
&&	su postgres -c "psql -U postgres -f sql/biblivre4.sql -d biblivre4" \
&&  cp target/Biblivre4.war $CATALINA_HOME/webapps \
&&	catalina.sh run

EXPOSE 8080