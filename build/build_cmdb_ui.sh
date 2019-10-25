#!/bin/bash

if [ ! -d "we-cmdb" ];
then 
    mkdir we-cmdb
fi

if [ ! -d "we-cmdb/.git" ];
then 
    rm -rf we-cmdb
    git clone https://github.com/WeBankPartners/we-cmdb.git
fi

cd we-cmdb
git pull origin master