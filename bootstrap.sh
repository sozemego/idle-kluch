#!/usr/bin/env bash
sudo apt-get update

#Install JDK
sudo apt-get -y install default-jdk

#set JAVA_HOME
JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:bin/javac::")

#download it to temp folder
cd /tmp
curl -O http://ftp.ps.pl/pub/apache/tomcat/tomcat-8/v8.5.32/bin/apache-tomcat-8.5.32.tar.gz
if [ $? -eq 0 ]; then
    echo 'downloaded tomcat successfuly'
else
    echo 'failed to download tomcat'
    exit 1
fi

#unpack it
sudo mkdir /opt/tomcat
sudo tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

#Configure tomcat
sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

#give tomcat user rights over the tomcat folder
cd /opt/tomcat
sudo chgrp -R tomcat /opt/tomcat
sudo chmod -R g+r conf
sudo chmod g+x conf
sudo chown -R tomcat webapps/ work/ temp/ logs/

sudo cp /vagrant/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml
sudo cp /vagrant/context.xml /opt/tomcat/webapps/manager/META-INF/context.xml
sudo cp /vagrant/setenv.sh /opt/tomcat/bin/setenv.sh

#set tomcat as service
sudo cat > /etc/systemd/system/tomcat.service <<- "EOF"
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false'

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

User=tomcat
Group=tomcat
UMask=0007
RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
EOF

#start the service
sudo systemctl daemon-reload
sudo systemctl start tomcat
sudo service tomcat restart

#allow tomcat to receive requests through the firewall
sudo ufw allow 8080

sudo systemctl enable tomcat

#apache
sudo apt-get install -y apache2
sudo ufw allow "Apache Full"

sudo usermod -G tomcat,www-data soze
sudo usermod -G tomcat,www-data vagrant
sudo chown -R www-data:www-data /var/www
sudo chmod -R 0775 /var/www

sudo chown -R tomcat:tomcat /opt/tomcat
sudo chmod -R 0775 /opt/tomcat
