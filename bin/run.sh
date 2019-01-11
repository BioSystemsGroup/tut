#!/bin/sh

##
# Used to execute the tut.  E.g.
# nohup ./bin/run.sh > keep-nohup.out 2>&1 &
#
# Time-stamp: <2016-09-29 16:06:53 gepr>
##

CLASSPATH=$CLASSPATH:./build/classes
CLASSPATH=$CLASSPATH:./lib/genson-1.3.jar
CLASSPATH=$CLASSPATH:./lib/logback-classic-0.9.28.jar
CLASSPATH=$CLASSPATH:./lib/logback-core-0.9.28.jar
CLASSPATH=$CLASSPATH:./lib/mason-18-with-src.jar
CLASSPATH=$CLASSPATH:./lib/slf4j-api-1.6.1.jar
export CLASSPATH

time java -Duser.language=US -Duser.country=US tut.Main $*
exit 0

