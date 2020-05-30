#!/bin/bash
rootPath=$PWD/../nginx/conf.d/biblivre.conf
nginxVersion=latest

result=`docker run --rm -t -a stdout --name tmp-nginx -v $PWD/nginx/conf.d/:/etc/nginx/conf.d:ro nginx nginx -c /etc/nginx/nginx.conf -t`

# Look for the word successful and count the lines that have it
# This validation could be improved if needed
successful=$(echo $result | grep successful | wc -l)

if [ $successful = 0 ]; then
    echo FAILED
    echo "$result"
    exit 1
else
    echo SUCCESS
    exit 0
fi
