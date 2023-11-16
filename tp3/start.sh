#!/bin/bash

podman build -t grpc .

podman rm -f grpc

podman run --rm --name grpc -d grpc tail -f /dev/null

podman ps