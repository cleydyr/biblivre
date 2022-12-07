#!/bin/bash

docker network create tmp >> /dev/null

docker run --network tmp -t --rm -d --name app tomcat:9-jdk8 >> /dev/null
docker run --network tmp --rm -t -a stdout --name tmp-nginx -v "$PWD/nginx/conf.d/:/etc/nginx/conf.d:ro" nginx nginx -c /etc/nginx/nginx.conf -t

exitCode=$?

if [ $exitCode -ne 0 ]; then
    echo FAILED
else
    echo SUCCESS
fi

docker stop app >> /dev/null
docker network rm tmp  >> /dev/null

exit $exitCode