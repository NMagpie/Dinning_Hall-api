FROM adoptopenjdk/openjdk11:latest
EXPOSE 8081
ADD target/dinning-hall-api.jar dinning-hall-api.jar
ADD /configDH.txt configDH.txt
ADD /menu.json menu.json
ENTRYPOINT ["java", "-jar", "/dinning-hall-api.jar"]