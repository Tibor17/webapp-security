# docker build -v -t tibor17/fat-jaas-wildfly17:latest -f Dockerfile .

FROM jboss/wildfly:17.0.1.Final
LABEL maintiner="Tibor Digaňa <tibordigana@apache.org>"
ARG WILDFLY_ADMIN_USER=admin
ARG WILDFLY_ADMIN_PASSWORD=Admin#70365
ARG POSTGRESQL_LIB=postgresql-42.2.8.jar
ARG DEPLOYMENT_ARCHIVE=webapp-jaas-wildfly17.war
ARG POSTGRESQL_LOGIN=postgres
ARG POSTGRESQL_PSWD=postgres
ARG POSTGRESQL_HOST=database
ARG POSTGRESQL_PORT=5432
ARG POSTGRESQL_DB=jaaswf8

USER root
RUN yum -y install net-tools && \
    yum -y clean all

USER jboss

ENV HOME=/opt/jboss \
    WILDFLY_ADMIN_USER=${WILDFLY_ADMIN_USER} \
    WILDFLY_ADMIN_PASSWORD=${WILDFLY_ADMIN_PASSWORD} \
    POSTGRESQL_LIB=${POSTGRESQL_LIB} \
    DEPLOYMENT_ARCHIVE=${DEPLOYMENT_ARCHIVE} \
    POSTGRESQL_LOGIN=${POSTGRESQL_LOGIN} \
    POSTGRESQL_PSWD=${POSTGRESQL_PSWD} \
    POSTGRESQL_HOST=${POSTGRESQL_HOST} \
    POSTGRESQL_PORT=${POSTGRESQL_PORT} \
    POSTGRESQL_DB=${POSTGRESQL_DB}

COPY --chown=jboss:jboss \
    docker-entrypoint.sh \
    add-jboss-module.cli \
    add-jboss-sql-driver.cli \
    add-jboss-xadatasource.cli \
    add-jboss-jaas.cli \
    target/$POSTGRESQL_LIB \
    target/$DEPLOYMENT_ARCHIVE \
    $HOME/

EXPOSE 8080/tcp 5432/tcp
# 9990/tcp

CMD ["./docker-entrypoint.sh"]
