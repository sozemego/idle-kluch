#!/usr/bin/env bash

sudo apt-get update
sudo apt-get install -y postgresql postgresql-contrib

sudo -u postgres -c "password $IDLE_KLUCH_DATABASE_PASSWORD"

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
