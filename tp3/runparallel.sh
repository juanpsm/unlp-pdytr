#!/bin/bash

if [ -z "$(podman ps | grep grpc)" ]; then
  echo "Start server: ./start.sh" 
fi

prog="podman exec grpc mvn -DskipTests package exec:java -Dexec.mainClass=pdytr.four.Client"

parallel "$prog {}" ::: "-Dexec.args=zero.txt" "-Dexec.args=one.txt"