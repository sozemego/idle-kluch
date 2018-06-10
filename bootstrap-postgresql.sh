#!/usr/bin/env bash

sudo apt-get update
sudo apt-get install -y postgresql postgresql-contrib

sudo -u postgres -c "password $POSTGRES_PASSWORD"

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

sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses='*'/" /etc/postgresql/9.5/main/postgresql.conf
sudo service postgresql restart