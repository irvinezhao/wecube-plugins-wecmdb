#!/bin/bash

set -ex
if ! docker --version &> /dev/null
then
    echo "must have docker installed"
    exit 1
fi

if ! docker-compose --version &> /dev/null
then
    echo  "must have docker-compose installed"
    exit 1
fi

if [ $# -ge 1 ]
then
  source $1
else
  source config.cfg
fi

cd `dirname $0`

sed "s~{{WECUBE_PLUGINS_WECMDB_IMAGE_NAME}}~$wecube_plugins_wecmdb_image_name~g" docker-compose-all.tpl > docker-compose.yml
sed -i "s~{{WECUBE_PLUGINS_WECMDB_SERVER_PORT}}~$wecube_plugins_wecmdb_server_port~g" docker-compose.yml  
sed -i "s~{{{CUSTOM_PARAM}}}~$custom_param~g" docker-compose.yml


docker-compose  -f docker-compose.yml  up -d
