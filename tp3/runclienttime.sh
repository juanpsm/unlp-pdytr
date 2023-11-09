#!/bin/bash

if [ -z "$(podman ps | grep grpc)" ]; then
  ./start.sh
fi

opt=$1
bytes=$2

if [ -z "$1" ]; then
  opt="example.grpc"
fi

if [ -z "$2" ]; then
  bytes="1024" # Default to 1024 bytes if no size is provided
fi

podman exec grpc mvn -DskipTests package exec:java -Dexec.mainClass=pdytr."$opt".Client -Dexec.args="$bytes"
