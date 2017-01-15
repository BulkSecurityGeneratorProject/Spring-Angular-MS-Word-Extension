#!/bin/bash

echo "Building..."
./mvnw -Pprod package -DskipTests

echo "Stopping service"
ssh root@87.238.165.25 '/etc/init.d/fsprocloud stop'

echo "Waiting..."
sleep 3

echo "Transfering new files"
./upload-to-production.sh

echo "Starting service"
ssh root@87.238.165.25 '/etc/init.d/fsprocloud start'

echo "Done!"