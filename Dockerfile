FROM cleydyr/tomcat:7-jdk8
RUN apt-get update
RUN apt-get install -y maven postgresql
ENV GITHUB_USER cleydyr
ENV BRANCH_NAME 5.x
ENTRYPOINT /etc/init.d/postgresql start \
&&	wget https://github.com/$GITHUB_USER/Biblivre-5/archive/$BRANCH_NAME.zip \
&&	unzip $BRANCH_NAME.zip -d /tmp && rm $BRANCH_NAME.zip \
&&	cd /tmp/Biblivre-5-$BRANCH_NAME/lib/ \
&&	sh maven_deps.sh \
&&	cd .. \
&&	mvn sass:update-stylesheets package \
&&	su postgres -c "psql -U postgres -f sql/createdatabase.sql" \
&&	su postgres -c "psql -U postgres -f sql/biblivre4.sql -d biblivre4" \
&&  cp target/Biblivre4.war $CATALINA_HOME/webapps \
&&	catalina.sh run

EXPOSE 8080