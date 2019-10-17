FROM openjdk:13-jdk-oraclelinux7 as build
ENV NEXUS https://repo1.maven.org/maven2
ENV GALLEON_VERSION 4.0.4.Final
WORKDIR /root
USER root:root
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

FROM azul/zulu-openjdk-alpine:8-jre
RUN mkdir -p /opt/wildfly
COPY --from=build /root/wildfly/ /opt/wildfly/
RUN chmod -R 755 /opt/wildfly
ENV JBOSS_HOME /opt/wildfly
ENV PATH $JBOSS_HOME/bin:$PATH
# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true
#-XX:+UnlockExperimentalVMOptions \
#-Xlog:gc+stats -XX:+UseZGC -XX:+ZUncommit -XX:ZUncommitDelay=5 \
#--illegal-access=warn
ENV JAVA_OPTS="-Xms32m -Xmx512m \
-XX:+UseNUMA \-Djava.net.preferIPv4Stack=true"
# Expose the ports we're interested in
EXPOSE 8080/tcp
EXPOSE 9990/tcp
CMD ["standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
