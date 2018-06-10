#!/usr/bin/env bash

sudo apt-get update
sudo apt-get install -y postgresql postgresql-contrib

sudo -u postgres psql -c "alter user postgres with password '$POSTGRES_PASSWORD'"

sudo -u postgres createdb idle_kluch
sudo -u postgres createdb idle_kluch_test
sudo -u postgres psql <<EOF
\c idle_kluch
CREATE EXTENSION citext;
CREATE EXTENSION "uuid-ossp";
\c idle_kluch_test
CREATE EXTENSION citext;
CREATE EXTENSION "uuid-ossp";
EOF

sudo usermod -a -G postgres soze
sudo usermod -a -G postgres vagrant
sudo chown -R postgres:postgres /etc/postgresql/9.5
sudo chmod -R 0775 /etc/postgresql/9.5

sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses='*'/" /etc/postgresql/9.5/main/postgresql.conf
sudo echo "host all all 0.0.0.0/0 md5" >> /etc/postgresql/9.5/main/pg_hba.conf
sudo service postgresql restart