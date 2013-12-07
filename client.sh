#!/bin/sh

DIR="$( cd "$( dirname "$0" )" && pwd )"

JAR=$DIR/target/cloudatlas-1.0-SNAPSHOT-jar-with-dependencies.jar

export CLASSPATH=$JAR

java -Djava.rmi.server.codebase=file:"$JAR" \
        -Djava.security.policy="$DIR/client.policy" \
        pl.edu.mimuw.cloudatlas.cli.Client \
        "$@"

