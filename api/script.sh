#!/usr/bin/env bash
echo 'installing the mvn project .....................'

mvn clean install -DskipTests

echo 'building the simba app image .......................'

docker build -f src/main/docker/Dockerfile -t simba .

echo 'docker compose down .........................'

docker-compose down

echo 'docker compose up .........................'

docker-compose up