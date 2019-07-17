#!/bin/bash
echo "Baixando e implantando a release mais recente do Biblivre 5"

sed -i \
	-e 's/127.0.0.1/'"${DATABASE_HOST_NAME}"'/g' \
	-e 's/5432/'"${DATABASE_PORT}"'/g' \
	-e 's/username="biblivre"/username="'"${DATABASE_USER}"'"/g' \
	-e 's/password="abracadabra"/password="'"${DATABASE_PASSWORD}"'"/g' \
	webapps/Biblivre4/META-INF/context.xml
sh ./bin/catalina.sh run
