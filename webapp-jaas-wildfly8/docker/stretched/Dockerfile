FROM openjdk:13-jdk-oraclelinux7 as build
ENV NEXUS https://repo1.maven.org/maven2
ENV GALLEON_VERSION 4.0.4.Final
WORKDIR /root
USER root:root
RUN rm -rf /usr/java/openjdk-13/jmods \
    && rm -rf /usr/java/openjdk-13/lib/src.zip \
    && rm -rf /usr/java/latest

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
COPY --from=build /usr/java/openjdk-13 /usr/share/jdk/
RUN chmod -R 755 /usr/share/jdk
ENV JBOSS_HOME /opt/wildfly
ENV JAVA_HOME /usr/share/jdk
ENV PATH $JAVA_HOME/bin:$JBOSS_HOME/bin:$PATH
# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true
ENV JAVA_OPTS="-Xmx512m --illegal-access=warn -Djava.net.preferIPv4Stack=true"
# Expose the ports we're interested in
EXPOSE 8080/tcp
EXPOSE 9990/tcp
CMD ["standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
