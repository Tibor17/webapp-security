#!/bin/sh

if [ ! -z "$WILDFLY_ADMIN_USER" ] && [ ! -z "$WILDFLY_ADMIN_PASSWORD" ]; then
    echo "Adding admin user..."
    cd $JBOSS_HOME/bin/ && $JBOSS_HOME/bin/add-user.sh $WILDFLY_ADMIN_USER $WILDFLY_ADMIN_PASSWORD --silent
fi

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/standalone.sh -b 127.0.0.1 -bmanagement 127.0.0.1 &
#exec "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0" &

while ! netstat -tulpn | grep LISTEN | grep 9990; do
    echo "Waiting for the server..."
    sleep 1s
done

if [ ! -e $JBOSS_HOME/bin/env.properties ]; then
    echo "mylib=$POSTGRESQL_LIB" >> $JBOSS_HOME/bin/env.properties
    echo "POSTGRESQL_LOGIN=$POSTGRESQL_LOGIN" >> $JBOSS_HOME/bin/env.properties
    echo "POSTGRESQL_PSWD=$POSTGRESQL_PSWD" >> $JBOSS_HOME/bin/env.properties
    echo "POSTGRESQL_HOST=$POSTGRESQL_HOST" >> $JBOSS_HOME/bin/env.properties
    echo "POSTGRESQL_PORT=$POSTGRESQL_PORT" >> $JBOSS_HOME/bin/env.properties
    echo "POSTGRESQL_DB=$POSTGRESQL_DB" >> $JBOSS_HOME/bin/env.properties
    chown -R jboss:root $JBOSS_HOME/bin/env.properties
fi




cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --command="echo tibor POSTGRESQL_DB=${POSTGRESQL_DB}"

#export _POSTGRESQL_DB_=abc



#. $JBOSS_HOME/bin/jboss-cli.sh -c --command="/subsystem=undertow/server=default-server/host=default-host/location=\/:remove"
cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --file=$HOME/add-jboss-module.cli

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/add-jboss-sql-driver.cli

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --file=$HOME/add-jboss-xadatasource.cli

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/add-jboss-jaas.cli

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --command=":shutdown"

while netstat -tulpn | grep LISTEN | grep 9990; do
    echo "Stopping the server..."
    sleep 1s
done

echo "The server stopped!"

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 &

while ! netstat -tulpn | grep LISTEN | grep :9990; do
    echo "Waiting for the server..."
    sleep 1s
done

cat $JBOSS_HOME/standalone/configuration/standalone.xml

echo "Deploying the enterprise web application..."

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh \
    --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --commands="deploy ~/$DEPLOYMENT_ARCHIVE"

cd $JBOSS_HOME/bin/ && \
    $JBOSS_HOME/bin/jboss-cli.sh \
    --version \
    --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --commands="deployment-info,ls deployment"

echo "FINISHED"

while netstat -tulpn | grep LISTEN | grep :9990; do
    sleep 1m
done
