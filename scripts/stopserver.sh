#!/bin/sh

# Usage:
# bash scripts/stopserver.sh [param]...
# * param: service name (discovery|gateway|account)

for var in "$@"
do
  PID=`ps -ef | grep java | grep war | grep $var | awk '{print $2}'`
  if [ -n "$PID" ]
  then
    echo "=====$var is running at" $PID
    kill -9 $PID
  else
    echo "=====$var isn't running====="
  fi
done