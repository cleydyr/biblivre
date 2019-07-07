#!/bin/bash
echo "Baixando e implantando a release mais recente do Biblivre 5"

echo 'https://github.com/cleydyr/biblivre/releases/download'`wget -SO-  https://github.com/cleydyr/biblivre/releases/latest 2>&1 >/dev/null | grep Location | cut -f2 -d " " | egrep -o "/v.*$"`"/Biblivre4.war" | tr -d "\r" | xargs wget
unzip Biblivre4.war -d webapps/Biblivre4
rm webapps/Biblivre4/WEB-INF/lib/postgresql-9.1-901.jdbc4.jar
sed -i \
	-e 's/127.0.0.1/'"${DATABASE_HOST_NAME}"'/g' \
	-e 's/5432/'"${DATABASE_PORT}"'/g' \
	-e 's/username="biblivre"/username="'"${DATABASE_USER}"'"/g' \
	-e 's/password="abracadabra"/password="'"${DATABASE_PASSWORD}"'"/g' \
	webapps/Biblivre4/META-INF/context.xml
sh ./bin/catalina.sh run