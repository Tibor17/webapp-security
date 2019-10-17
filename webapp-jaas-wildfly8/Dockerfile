# docker build -v -t tibor17/fat-jaas-wildfly8:latest -f Dockerfile .

FROM jboss/wildfly:8.2.1.Final

# MAINTAINER is deprecated in Docker
LABEL org.label-schema.schema-version=1.0 \
      org.label-schema.name="web application with WildFly 8.2.1" \
      org.label-schema.vendor="Tibor Digaňa <tibordigana@apache.org>" \
      org.label-schema.maintainer="Tibor Digaňa <tibordigana@apache.org>"

ARG WILDFLY_ADMIN_USER
ARG WILDFLY_ADMIN_PASSWORD
ARG POSTGRESQL_LIB
ARG DEPLOYMENT_ARCHIVE
ARG POSTGRESQL_LOGIN
ARG POSTGRESQL_PSWD
ARG POSTGRESQL_HOST
ARG POSTGRESQL_PORT
ARG POSTGRESQL_DB
# DEBUG
# WARN
# INFO
ARG LOG_LEVEL
ARG TZ

ENV TZ=${TZ:-Europe/Bratislava}

USER root

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

RUN yum -y install net-tools && \
    yum -y clean all

USER jboss

ENV HOME=/opt/jboss \
    WILDFLY_ADMIN_USER=${WILDFLY_ADMIN_USER:-admin} \
    WILDFLY_ADMIN_PASSWORD=${WILDFLY_ADMIN_PASSWORD:-Admin#70365} \
    POSTGRESQL_LIB=${POSTGRESQL_LIB:-postgresql-42.2.8.jar} \
    DEPLOYMENT_ARCHIVE=${DEPLOYMENT_ARCHIVE:-webapp-jaas-wildfly8.war} \
    POSTGRESQL_LOGIN=${POSTGRESQL_LOGIN:-postgres} \
    POSTGRESQL_PSWD=${POSTGRESQL_PSWD:-postgres} \
    POSTGRESQL_HOST=${POSTGRESQL_HOST:-database} \
    POSTGRESQL_PORT=${POSTGRESQL_PORT:-5432} \
    POSTGRESQL_DB=${POSTGRESQL_DB:-jaaswf8} \
    LOG_LEVEL=${LOG_LEVEL:-WARN}

COPY --chown=jboss:jboss \
    docker-entrypoint.sh \
    *.cli \
    target/$POSTGRESQL_LIB \
    target/$DEPLOYMENT_ARCHIVE \
    $HOME/

# https://stackoverflow.com/questions/46362935/how-to-add-a-docker-health-check-to-test-a-tcp-port-is-open
# https://docs.docker.com/engine/reference/builder/#healthcheck
HEALTHCHECK --start-period=40s \
            --interval=5s \
            --timeout=3s \
            --retries=12 \
#            CMD wget --fail --quiet --tries=1 --spider http://localhost:8081/ws/ || exit 1
            CMD netstat -tln | grep LISTEN | grep 8080 > /dev/null; if [ 0 != $? ]; then exit 1; fi;


EXPOSE 8080/tcp 5432/tcp
#  9990/tcp
CMD ["./docker-entrypoint.sh"]
