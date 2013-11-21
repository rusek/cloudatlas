#!/bin/sh

DIR="$( cd "$( dirname "$0" )" && pwd )"

JAR=$DIR/target/cloudatlas-1.0-SNAPSHOT-jar-with-dependencies.jar

export CLASSPATH=$JAR

java -Djava.rmi.server.codebase=file:"$JAR" \
        -Djava.rmi.server.hostname=localhost -Djava.security.policy="$DIR/server.policy" \
        pl.edu.mimuw.cloudatlas.agent.Main
