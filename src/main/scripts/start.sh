#!/bin/sh

set -e

EXECUTABLE=target/poormans-service-manager-assembly-0.0.1-SNAPSHOT.jar
DEFAULT_JAVA_OPTS='-XX:+UseSerialGC -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=50 -XX:PermSize=8m -Xms3m -server'
: ${JAVA_OPTS:=$DEFAULT_JAVA_OPTS}

java $JAVA_OPTS -jar $EXECUTABLE
