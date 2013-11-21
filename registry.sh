#!/bin/sh

DIR="$( cd "$( dirname "$0" )" && pwd )"

JAR=$DIR/target/cloudatlas-1.0-SNAPSHOT-jar-with-dependencies.jar

export CLASSPATH=$JAR

rmiregistry &
