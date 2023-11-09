FROM alpine:3.14 as base

ENV MAVEN_VERSION 3.8.8

RUN apk update && apk add --no-cache \
    openjdk8 wget gcompat protobuf-dev \
    && wget -O /tmp/maven.tar.gz https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    && tar -zxvf /tmp/maven.tar.gz -C /etc/  \
    && mv /etc/apache-maven-$MAVEN_VERSION /etc/maven \
    && echo "PATH=/etc/maven/bin:$PATH" > /etc/environment 

ENV PATH="/etc/maven/bin:${PATH}" 

RUN mvn archetype:generate \
-DgroupId=pdytr.example.grpc \
-DartifactId=grpc-hello-server \
-DarchetypeArtifactId=maven-archetype-quickstart \
-DinteractiveMode=false

WORKDIR /grpc-hello-server

COPY pom.xml pom.xml
COPY src src

RUN mvn -DskipTests -e -X package
