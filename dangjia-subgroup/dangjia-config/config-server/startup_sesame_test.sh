#!/bin/bash
export JAVA_HOME=/usr/java/jdk1.8.0_131
export PATH=$JAVA_HOME/bin
echo “service $1 $2 start…  nohup java -Xms256m -Xmx1024m -Dspring.profiles.active=test -jar $1/$2 ”
cd $1
wz=pwd
echo "$wz"
java -Xms256m -Xmx1024m -Dspring.profiles.active=test -jar $1/$2 &
echo “service start success”
