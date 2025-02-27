#!/bin/sh

# Usage:
# bash scripts/flyway.sh param1
# * param1: flyway command (Migrate|Clean|...)

PROPERTY_FILE=./account-service/src/main/resources/datasource.properties

function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat $PROPERTY_FILE | grep "$PROP_KEY" | cut -d'=' -f2-`
   echo $PROP_VALUE
}

USER=$(getProperty "spring.datasource.user")
PASSWORD=$(getProperty "spring.datasource.password")
URL=$(getProperty "spring.datasource.url")
LOCATION="classpath:db/migration/account"

rm -rf ./account-service/build/resources/main/db
cp -r ./account-service/src/main/resources/db ./account-service/build/resources/main
./gradlew -p account-service flyway$1 "-i" "-Dflyway.user=$USER" "-Dflyway.password=$PASSWORD" "-Dflyway.url=$URL" "-Dflyway.locations=$LOCATION" "-Dflyway.schemas=db1_account"
# read -p "Press enter to continue"