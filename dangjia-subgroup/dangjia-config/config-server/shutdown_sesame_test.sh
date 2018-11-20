#!/bin/bash
echo "Stopping $1 Application"
pid=`ps -ef | grep $1 | grep -v grep | awk '{print $2}'`
echo “进程号:$pid ”
if [ -n "$pid" ]
then
    kill -9 $pid
    echo "删除进程号:$pid"
    
fi
