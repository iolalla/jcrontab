#!/bin/bash
#
echo "starting Jcrontab" 
#/usr/local/java/bin/java -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,address=6667,suspend=y -cp /home/iolalla/src/jcrontab/jar/jcrontab.jar:/home/iolalla/src/jcrontab/jar/log4j-1.2.8.jar:/home/iolalla/src/jcrontab/jar/xerces.jar:. org.jcrontab.Jcrontab jcrontab.properties
/usr/local/java/bin/java -cp /home/iolalla/src/jcrontab/jar/jcrontab.jar:/home/iolalla/src/jcrontab/jar/log4j-1.2.8.jar:/home/iolalla/src/jcrontab/jar/xerces.jar:. org.jcrontab.Jcrontab jcrontab.properties
