#!/bin/bash

if [ -z "$(podman ps | grep grpc)" ]; then
  echo "Start server: ./start.sh" 
fi

opt=$1
if [ -z "$1" ]; then
  opt="example.grpc"
fi

args="$2"
if [ -z "$2" ]; then
  args=""
fi

podman exec grpc mvn -DskipTests package exec:java -Dexec.mainClass=pdytr."$opt".Client -Dexec.args="$args"
