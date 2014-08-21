#!/bin/bash
export HIVE_HOME=/usr/lib/hive
export LIBJARS=$HIVE_HOME/lib/hive-metastore.jar,$HIVE_HOME/lib/datanucleus-core-3.2.10.jar,$HIVE_HOME/lib/datanucleus-rdbms-3.2.9.jar,$HIVE_HOME/lib/datanucleus-api-jdo-3.2.6.jar,/etc/hive/conf.dist
export HADOOP_CLASSPATH=$HIVE_HOME/lib/hive-metastore.jar:$HIVE_HOME/lib/datanucleus-core-3.2.10.jar:$HIVE_HOME/lib/datanucleus-rdbms-3.2.9.jar:$HIVE_HOME/lib/datanucleus-api-jdo-3.2.6.jar:/etc/hive/conf.dist
hadoop jar build/libs/impatient.jar -D mapreduce.job.queuename=development -libjars $LIBJARS
# export HADOOP_USER_CLASSPATH_FIRST="true"
# export HADOOP_CLASSPATH=build/libs/impatient.jar
# /usr/bin/hive --service jar build/libs/impatient.jar impatient.Main
