#!/bin/bash
echo "Hello, User!"
docker build -t dinning-hall dinning-hall-api/
docker build -t kitchen kitchen-api/
docker network create pr-lab
docker container run --net pr-lab --publish 8080:8080 --name kitchen kitchen
docker network inspect pr-lab
read -p "Press any key to exit ..."
