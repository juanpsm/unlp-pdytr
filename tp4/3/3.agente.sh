#!/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Usage: $0 [read|write|readwrite] localPath remotePath"
    exit 1
fi

op=$1
local_path=$2
remote_path=$3

java -cp ../lib/jade.jar:classes jade.Boot -gui -container  -host localhost -agents "mol:FTPAgent($op,$local_path,$remote_path)"
