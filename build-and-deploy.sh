#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Building..."
./mvnw -Pprod package -DskipTests

echo "Stopping service"
ssh root@87.238.165.25 '/etc/init.d/fsprocloud stop'

echo "Waiting..."
sleep 3

echo "Transfering new files"
rsync -rltz --progress --delete \
--perms \
--chmod=u=rwX,g=rwX,o=rX \
--rsh "ssh" \
"$DIR"/target/*.war root@87.238.165.25:/var/fsprocloud/


echo "Starting service"
ssh root@87.238.165.25 '/etc/init.d/fsprocloud start'

echo "Done!"