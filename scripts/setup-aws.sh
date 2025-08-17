wget https://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm
sudo yum localinstall mysql57-community-release-el7-11.noarch.rpm
sudo yum provides mysql-community-server
sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
sudo yum install mysql-community-server-5.7.12-1.el7.x86_64
sudo systemctl start mysqld
sudo cat /var/log/mysqld.log | grep "A temporary password"
sudo mysql_secure_installation

curl -L https://corretto.aws/downloads/latest/amazon-corretto-11-x64-linux-jdk.rpm -o jdk11.rpm
sudo yum localinstall jdk11.rpm
sudo /usr/sbin/alternatives --config java

sudo yum install iptables
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 9000

read -p "Press enter to continue"