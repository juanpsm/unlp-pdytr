# Practica 3: gRPC

## Primer paso:
´´´bash
mkdir app

podman build -t grpc .

´´´

## Segundo paso

### En terminal 1:
´´´bash
podman run --rm --name ej1  grpc mvn -DskipTests package exec:java -Dexec.mainClass=pdytr.example.grpc.App
´´´

### En terminal 2: 
´´´bash
podman exec ej1 mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client
´´´
