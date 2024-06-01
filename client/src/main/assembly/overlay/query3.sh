#!/bin/bash

PATH_TO_CODE_BASE=`pwd`

# Add arguments as java opts
JAVA_OPTS="$JAVA_OPTS $*"

MAIN_CLASS="ar.edu.itba.pod.client.query3.Client"


java  $JAVA_OPTS -cp 'lib/jars/*' $MAIN_CLASS