#!/bin/sh


# DIRNAME=`dirname "$0"`

echo "Running the server with arguments $@"

#if [ -z "$WILDFLY_ADMIN_USER" ] && [ -z "$WILDFLY_ADMIN_PASSWORD" ]; then
#    $JBOSS_HOME/bin/add-user.sh "$WILDFLY_ADMIN_USER" "$WILDFLY_ADMIN_PASSWORD" --silent
#fi

#exec "$@" > /dev/null &
exec "$@"

#echo "Waiting for the server ... "

# nc -w 1 -v -l -p 9990
#while ! lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null; do sleep 1; done

#sleep 1

#echo "The server has started"
