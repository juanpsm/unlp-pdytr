#!/bin/bash

if [ -z "$(podman ps | grep grpc)" ]; then
  ./start
fi

opt=$1
if [ -z "$1" ]; then
  opt="example.grpc"
fi

podman exec grpc mvn -DskipTests package exec:java -Dexec.mainClass=pdytr."$opt".Client
