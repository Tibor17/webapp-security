#!/bin/sh

function wait_for_server_up() {
    while ! netstat -tulpn | grep LISTEN | grep 9990; do
        echo "Waiting for the server..."
        sleep 1s
    done
}

function wait_for_server_shutdown() {
    while netstat -tulpn | grep LISTEN | grep 9990; do
        echo "Waiting for the server stop."
        if [ -z $1 ]; then
            sleep 1s
        else
            sleep $1
        fi
    done
}

if [ ! -z "$WILDFLY_ADMIN_USER" ] && [ ! -z "$WILDFLY_ADMIN_PASSWORD" ]; then
    echo "Adding admin user..."
    . $JBOSS_HOME/bin/add-user.sh --user "$WILDFLY_ADMIN_USER" \
                                  --password "$WILDFLY_ADMIN_PASSWORD" \
                                  --group 'admin' \
                                  --silent
fi

. $JBOSS_HOME/bin/standalone.sh -b 127.0.0.1 -bmanagement 127.0.0.1 &

wait_for_server_up

echo "POSTGRESQL_LIB_PATH=$HOME/$POSTGRESQL_LIB" >> env.properties
echo "POSTGRESQL_LOGIN=$POSTGRESQL_LOGIN" >> env.properties
echo "POSTGRESQL_PSWD=$POSTGRESQL_PSWD" >> env.properties
echo "POSTGRESQL_HOST=$POSTGRESQL_HOST" >> env.properties
echo "POSTGRESQL_PORT=$POSTGRESQL_PORT" >> env.properties
echo "POSTGRESQL_DB=$POSTGRESQL_DB" >> env.properties
echo "LOG_LEVEL=$LOG_LEVEL" >> env.properties

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/remove-welcome-content.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/disable_management-web-console_and_hot-deployment.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/remove-unused-jboss-modules.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --file=$HOME/add-jboss-logger.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --file=$HOME/add-jboss-module.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/add-jboss-sql-driver.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --properties=env.properties \
    --file=$HOME/add-jboss-xadatasource.cli

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --file=$HOME/add-jboss-jaas.cli

echo "Stopping the server..."

. $JBOSS_HOME/bin/jboss-cli.sh --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --command=":shutdown"

wait_for_server_shutdown

echo "The server is stopped!"

export JAVA_OPTS="-Xms64m -Xmx256m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true \
-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps"

. $JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 127.0.0.1 &

wait_for_server_up

#cat $JBOSS_HOME/standalone/configuration/standalone.xml

echo "Deploying the enterprise web application(s)..."
DEPLOYMENTS=$HOME/*.war
for f in $DEPLOYMENTS
do
  echo "Deploying the web archive '$f' file..."
  . $JBOSS_HOME/bin/jboss-cli.sh \
      --connect --controller=127.0.0.1:9990 \
      --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
      --commands="deploy $f"
done

. $JBOSS_HOME/bin/jboss-cli.sh \
    --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --commands="deployment-info"

. $JBOSS_HOME/bin/jboss-cli.sh \
    --connect --controller=127.0.0.1:9990 \
    --user=$WILDFLY_ADMIN_USER --password=$WILDFLY_ADMIN_PASSWORD \
    --commands="ls deployment"

echo "FINISHED"

wait_for_server_shutdown '1m'
