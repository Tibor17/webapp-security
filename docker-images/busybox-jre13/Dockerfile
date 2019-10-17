FROM openjdk:13 as jre
LABEL maintiner="Tibor Digaňa <tibordigana@apache.org>"
RUN ["jlink", "--compress=2", \
     "--strip-debug", \
     "--no-header-files", \
     "--no-man-pages", \
     "--module-path", "/usr/java/openjre-13", \
     "--add-modules", "java.sql.rowset,java.xml.crypto,java.management.rmi,java.desktop,java.security.jgss,java.compiler,java.instrument,java.scripting,jdk.security.auth,jdk.xml.dom,jdk.unsupported", \
#     "--add-modules", "java.se,jdk.security.auth,jdk.xml.dom,jdk.unsupported", \
     "--output", "/root/openjre-13"]

FROM tibor17/busybox-with-gnulibs:latest
RUN mkdir -p /usr/share/jre
COPY --from=jre /root/openjre-13 /usr/share/jre/
WORKDIR /root
USER root:root
RUN chmod -R 755 /usr/share/jre
ENV JAVA_HOME /usr/share/jre
ENV PATH $JAVA_HOME/bin:$PATH
