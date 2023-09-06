#!/bin/sh

# Usage:
# bash scripts/postBuildConfiguration.sh

BASE_DIR="$( cd "$( dirname "$0" )" && pwd -P )/.."

nohup bash -c "for run in {1..5}; do sleep 1; sed -i 's/value=\"querydsl\"/value=\"main\"/' $BASE_DIR/.classpath; done"  >/dev/null 2>&1 &
