#!/usr/bin/env bash

echo 'pulling the docker image'

docker pull etherpad/etherpad

docker run  --publish 9001:9001 --name etherpad -d etherpad/etherpad



docker exec -it etherpad /bin/sh

touch /opt/etherpad-lite/APIKEY.txt
cat  /opt/etherpad-lite/APIKEY.txt

