FROM openjdk:13-jdk-oraclelinux7 as build
ENV NEXUS https://repo1.maven.org/maven2
ENV GALLEON_VERSION 4.0.4.Final
WORKDIR /root
USER root:root
RUN ["jlink", "--compress=2", \
     "--strip-debug", \
     "--no-header-files", \
     "--no-man-pages", \
     "--module-path", "/usr/java/openjdk-13", \
     "--add-modules", "java.sql.rowset,java.xml.crypto,java.management.rmi,java.desktop,java.security.jgss,java.compiler,java.instrument,java.scripting,jdk.security.auth,jdk.xml.dom,jdk.unsupported", \
#     "--add-modules", "java.se,jdk.security.auth,jdk.xml.dom,jdk.unsupported", \
     "--output", "/root/openjdk-13"]
ADD ${NEXUS}/org/jboss/galleon/galleon-cli/${GALLEON_VERSION}/galleon-cli-${GALLEON_VERSION}.jar /root/
RUN mv /root/galleon-cli-${GALLEON_VERSION}.jar /root/galleon-cli.jar
COPY ./galleon.sh /root/
COPY ./galleon-cli-logging.properties /root/
RUN ["/root/galleon.sh", \
    "install", \
    "wildfly:17.0#17.0.1.Final", \
    "--verbose", \
    "--dir=/root/wildfly", \
    "--layers=jaxrs,cdi"]

FROM debian:stretch-slim
ENV LANG C.UTF-8
RUN mkdir -p /opt/wildfly
COPY --from=build /root/wildfly/ /opt/wildfly/
RUN chmod -R 755 /opt/wildfly
RUN mkdir -p /usr/share/jdk
COPY --from=build /root/openjdk-13 /usr/share/jdk/
RUN chmod -R 755 /usr/share/jdk
ENV JBOSS_HOME /opt/wildfly
ENV JAVA_HOME /usr/share/jdk
ENV PATH $JAVA_HOME/bin:$JBOSS_HOME/bin:$PATH
# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true
#-XX:+UseShenandoahGC -XX:ShenandoahGuaranteedGCInterval=1100 -XX:ShenandoahUncommitDelay=0 \
ENV JAVA_OPTS="-Xms32m -Xmx512m \
-XX:+UseNUMA \
-XX:+UnlockExperimentalVMOptions \
-Xlog:gc+stats -XX:ZUncommitDelay=5 \
--illegal-access=warn -Djava.net.preferIPv4Stack=true"
# Expose the ports we're interested in
EXPOSE 8080/tcp
EXPOSE 9990/tcp
CMD ["standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
