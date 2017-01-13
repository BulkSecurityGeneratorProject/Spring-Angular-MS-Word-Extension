#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# gulp default

rsync -rltz --progress --delete \
--perms \
--chmod=u=rwX,g=rwX,o=rX \
--rsh "ssh" \
"$DIR"/target/*.war root@87.238.165.25:/var/fsprocloud/

#--exclude='- /application-prod.yml' \
#--exclude='- /uploads' \
