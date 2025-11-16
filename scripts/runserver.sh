#!/bin/sh

# Usage:
# bash scripts/runserver.sh [param]...
# * param: service name (discovery|gateway|account)

for var in "$@"
do
  PID=`ps -ef | grep java | grep war | grep $var | awk '{print $2}'`
  if [ -n "$PID" ]
  then
    echo "=====$var is running at" $PID
  else
    # ./gradlew build -p $var-service
    echo "=====$var isn't running====="
    if [ "$var" == "discovery" ]; then
        options="-Xmx32m"
    elif [ "$var" == "account" ]; then
        options="-Djava.security.egd=file:/dev/./urandom"
    else
	      options=""
    fi
    nohup java -jar $options -XX:TieredStopAtLevel=1 **/build/libs/$var-service*.war >/dev/null 2>&1 &
  fi
done