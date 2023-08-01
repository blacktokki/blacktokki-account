#!/bin/sh

# Usage:
# bash scripts/setup.sh param1 param2
# * param1: database username
# * param2: database password, secret key

DATASOURCE_FILE=./account-service/src/main/resources/datasource.properties
MYSQL_CONFIG_FILE=/etc/mysql./my.cnf
SECRET_FILE=./account-service/src/main/resources/secret.properties
FRONTEND_DIRECTORY=./frontend

sudo apt-get update
sudo apt-get --assume-yes install mysql-server-5.7
sudo mysql -u root -p" " -e"
create user '$1'@'%' identified by '$2';
GRANT ALL PRIVILEGES ON *.* TO '$1'@'%';
flush privileges;
SET character_set_server = 'utf8';
create database db1_account;
"

sudo apt-get --assume-yes install openjdk-11-jdk
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 9000

echo "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/?rewriteBatchedStatements=true&characterEncoding=UTF-8" > $DATASOURCE_FILE  # &profileSQL=true&logger=Slf4JLogger&maxQuerySizeToLog=999999
echo "spring.datasource.username=$1" >> $DATASOURCE_FILE
echo "spring.datasource.password=$2" >> $DATASOURCE_FILE
echo "jwt.secret=$2" >> $SECRET_FILE

## replication DB
echo "[mysqld]" >> $MYSQL_CONFIG_FILE
echo "log-bin=/var/log/mysql/mysql-bin.log" >> $MYSQL_CONFIG_FILE
echo "server-id=1" >> $MYSQL_CONFIG_FILE
echo "expire_logs_days=7" >> $MYSQL_CONFIG_FILE
echo "bind-address=0.0.0.0" >> $MYSQL_CONFIG_FILE
sudo systemctl restart mysql

read -p "Press enter to continue"
