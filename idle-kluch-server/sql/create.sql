DROP TABLE IF EXISTS kingdoms CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS tiles;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS buildings CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS storage_units;

CREATE TABLE users (
  user_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  deleted boolean DEFAULT FALSE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  username citext NOT NULL UNIQUE
);

CREATE TABLE tiles (
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  PRIMARY KEY (x, y)
);

CREATE TABLE resources (
  resource_id uuid NOT NULL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE
);

INSERT INTO resources VALUES
  (uuid_generate_v4(), 'Wood'),
  (uuid_generate_v4(), 'Stone'),
  (uuid_generate_v4(), 'Plank'),
  (uuid_generate_v4(), 'Brick');

CREATE TABLE kingdoms (
  kingdom_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(32) NOT NULL,
  owner uuid NOT NULL,
  CONSTRAINT FK_KINGDOM_USER FOREIGN KEY (owner) REFERENCES users(user_id)
);

CREATE TABLE buildings (
  building_id uuid NOT NULL PRIMARY KEY,
  definition_id VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(100) NOT NULL,
  kingdom_id uuid NOT NULL,
  x int NOT NULL,
  y int NOT NULL,
  CONSTRAINT FK_KINGDOM_BUILDING FOREIGN KEY (kingdom_id) REFERENCES kingdoms(kingdom_id)
);

CREATE TABLE warehouses (
  building_id uuid NOT NULL PRIMARY KEY
);

CREATE TABLE storage_units (
  building_id uuid NOT NULL,
  resource_id uuid NOT NULL,
  amount INTEGER NOT NULL,
  capacity INTEGER NOT NULL,
  CONSTRAINT FK_STORAGE_UNIT_BUILDING FOREIGN KEY (building_id) REFERENCES warehouses(building_id),
  CONSTRAINT FK_STORAGE_UNIT_RESOURCE FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
);
