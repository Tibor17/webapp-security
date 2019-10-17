# docker build -t tibor17/busybox-with-gnulibs:latest -f Dockerfile .

FROM centos:7 as centos
LABEL maintiner="Tibor Digaňa <tibordigana@apache.org>"
RUN yum -y install unzip && \
    mkdir -p /busybox/lib && \
    cp -p /lib64/libbz2.so.1 /busybox/lib/libbz2.so.1



FROM debian:stretch-slim as debian

#RUN apt-get update && \
#    DEBIAN_FRONTEND=noninteractive apt-get install -y net-tools && \
#    apt-get clean && \
#    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /busybox/lib && \
    cp -p /lib/x86_64-linux-gnu/libdl.so.2 /busybox/lib/libdl.so.2 && \
    cp -p /lib/x86_64-linux-gnu/librt.so.1 /busybox/lib/librt.so.1 && \
    cp -p /lib/x86_64-linux-gnu/libz.so.1 /busybox/lib/libz.so.1 && \
    cp -p /lib/x86_64-linux-gnu/libgcc_s.so.1 /busybox/lib/libgcc_s.so.1
#    && \
#    cp -p /lib/x86_64-linux-gnu/libnss_files.so.2 /busybox/lib/libnss_files.so.2



# https://hub.docker.com/_/busybox
# 1.31.0-glibc has segmentation fault
FROM busybox:1.30.1-glibc
WORKDIR /root
USER root:root
ENV LANG C.UTF-8
COPY --from=centos /busybox/lib /lib/
COPY --from=debian /busybox/lib /lib/
