#!/bin/sh

# Usage:
# bash scripts/setup.sh param1 param2 param3 param4
# * param1: github username
# * param2: database username
# * param3: database password, secret key

DATASOURCE_FILE=./account-service/src/main/resources/datasource.properties
SECRET_FILE=./account-service/src/main/resources/secret.properties
FRONTEND_DIRECTORY=./frontend

git config user.email "$1@github.com"
git config user.name "$1"
git config credential.helper store

sudo apt-get update
sudo apt-get --assume-yes install mysql-server-5.7
sudo mysql -u root -p" " -e"
create user '$2'@'%' identified by '$3';
GRANT ALL PRIVILEGES ON *.* TO '$2'@'%';
flush privileges;
SET character_set_server = 'utf8';
create database db1_account;
"

sudo apt-get --assume-yes install openjdk-11-jdk
echo "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/?rewriteBatchedStatements=true&characterEncoding=UTF-8" > $DATASOURCE_FILE  # &profileSQL=true&logger=Slf4JLogger&maxQuerySizeToLog=999999
echo "spring.datasource.username=$2" >> $DATASOURCE_FILE
echo "spring.datasource.password=$3" >> $DATASOURCE_FILE
echo "jwt.secret=$3" >> $SECRET_FILE

# curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
# sudo apt-get --assume-yes install nodejs
# sudo npm install -g concurrently expo-cli
# npm --prefix $FRONTEND_DIRECTORY install
# echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p

read -p "Press enter to continue"
