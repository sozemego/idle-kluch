sudo apt-get update

#Install JDK
sudo apt-get -y install default-jdk

#set JAVA_HOME
JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:bin/javac::")

#Configure tomcat
sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

#download it to temp folder
cd /tmp
curl -O http://ftp.man.poznan.pl/apache/tomcat/tomcat-8/v8.5.31/bin/apache-tomcat-8.5.31.tar.gz

#unpack it
sudo mkdir /opt/tomcat
sudo tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

sudo cp /vagrant/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml
sudo cp /vagrant/context.xml /opt/tomcat/webapps/manager/META-INF/context.xml

#give tomcat user rights over the tomcat folder
cd /opt/tomcat
sudo chgrp -R tomcat /opt/tomcat
sudo chmod -R g+r conf
sudo chmod g+x conf
sudo chown -R tomcat webapps/ work/ temp/ logs/

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
Environment='CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC'
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'

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

#allow tomcat to receive requests through the firewall
sudo ufw allow 8080

sudo systemctl enable tomcat
