#!/bin/sh

DIR="$( cd "$( dirname "$0" )" && pwd )"

JAR=$DIR/target/cloudatlas-1.0-SNAPSHOT-jar-with-dependencies.jar

export CLASSPATH=$JAR

HOSTNAME="$( java pl.edu.mimuw.cloudatlas.agent.GetHostname "$@" )"

err=$?
if [ $err -ne 0 ]
then
        echo "Could not determine hostname"
        exit 1
fi

java -Djava.rmi.server.codebase=file:"$JAR" \
        -Djava.rmi.server.hostname="$HOSTNAME" -Djava.security.policy="$DIR/agent.policy" \
        pl.edu.mimuw.cloudatlas.agent.Main \
        "$@"
