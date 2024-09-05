@echo off

chcp 65001

call mvn clean package

echo Deleting previous jar
del "%CD%\src\main\docker\*.jar"

echo Moving copy from %CD%\target\pepega*.jar to %CD%\src\main\docker directory
copy "%CD%\target\pepegaVpnManager-1.0-SNAPSHOT.jar" "%CD%\src\main\docker\pepegaVpnManager-1.0-SNAPSHOT.jar" /Y

echo Changing working directory to docker
cd "src/main/docker"

echo Decomposing existing docker container
docker-compose down

echo Clearing old image of pepega-vpn-manager
docker rmi pepega-vpn-manager:latest

echo Composing container with a new image
docker-compose up