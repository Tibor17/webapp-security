FROM maven:3.6.1-jdk-13 as build

ARG USER_HOME_DIR="/root"

RUN ["jlink", "--compress=2", \
     "--strip-debug", \
     "--no-header-files", \
     "--no-man-pages", \
#	 "--module-path", "/usr/java/openjdk-13/jmods/", \
     "--add-modules", "java.base,java.logging,java.naming,java.xml", \
# for Wildfly
#     "--add-modules", "java.base,java.logging,java.sql,java.desktop,java.management,java.naming,jdk.unsupported", \
     "--output", "/openjdk-13"]


# proposed by contributor 'duncaan' in correto-8-docker
# used in the official OpenJdk 8
# the size of inage is 55 MB, and with Java (3 modules) the size is 89 MB
FROM debian:stretch-slim

# do not use 'amazonlinux' from Correto JDK because the size of image is 197 MB.
#FROM amazonlinux:2

# debian used on official OpenJdk 11
# the size of inage is 104 MB
#FROM debian:buster-slim

# original and official oraclelinux used on OpenJdk 13
# the size of inage is 152 MB
#FROM oraclelinux:7-slim

# Alpine Linux uses MUSL as a Standard C library.
# Oracle's Java for linux depends on GNU Standard C library (gclib).
# https://java.develop-bugs.com/article/11798880/Docker+alpine+%2b+oracle+java%3a+cannot+find+java
#FROM alpine:latest

LABEL maintainer="Tibor Digaňa"

WORKDIR ${USER_HOME_DIR:-/root}

RUN mkdir -p /usr/share/jdk

COPY --from=build --chown=501:dialout /openjdk-13 /usr/share/jdk/
COPY --from=build /usr/share/maven /usr/share/maven/
ADD --chown=501:dialout ./pom.xml ${USER_HOME_DIR:-/root}/
ADD --chown=501:dialout ./webapp-jaas-wildfly8 ${USER_HOME_DIR:-/root}/webapp-jaas-wildfly8/

ENV LANG C.UTF-8

# for oraclelinux
#ENV LANG en_US.UTF-8

ENV JAVA_VERSION 13
ENV MAVEN_HOME /usr/share/maven
ENV JAVA_HOME /usr/share/jdk
ENV PATH $JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
ENV DOCKER_HOME ${USER_HOME_DIR:-/root}
ENV MAVEN_OPTS="-Xmx128m --illegal-access=warn"

VOLUME ${USER_HOME_DIR:-/root}/webapp-jaas-wildfly8/target
ENTRYPOINT ["/bin/sh", "-c"]
CMD ["mvn -V package -DskipTests -DskipITs"]
