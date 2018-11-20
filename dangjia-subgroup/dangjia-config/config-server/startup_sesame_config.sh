#!/bin/bash
nohup  java -jar -Xms256m -Xmx512m /home/dangjia/dangjia-config/config-server-0.0.1-SNAPSHOT.jar &
echo $! > /var/run/config-server-0.0.1-SNAPSHOT.pid