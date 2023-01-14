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
    echo "=====$var isn't running====="
    nohup java -jar **/build/libs/$var-service*.war >/dev/null 2>&1 &
  fi
done