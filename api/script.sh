#!/usr/bin/env bash
echo 'installing the mvn project .....................'

mvn clean install -DskipTests

echo 'building the simba app image .......................'

docker build -f src/main/docker/Dockerfile -t simba .


echo 'pulling the etherpad docker image'

docker pull etherpad/etherpad

touch APIKEY.txt
chmod 777 APIKEY.txt
echo 'fa8cce291d03acaf1dce7d137f73ce60aa2eeebdec77be42bcb8461d0e4278ea' > APIKEY.txt

docker run -v $(pwd)/APIKEY.txt:/opt/etherpad-lite/APIKEY.txt --publish 9001:9001 --name etherpad  etherpad/etherpad


echo 'docker compose down .........................'

docker-compose down

echo 'docker compose up .........................'

docker-compose up