DROP TABLE IF EXISTS kingdoms;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS tiles;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS buildings;
DROP TABLE IF EXISTS warehouses;
DROP TABLE IF EXISTS warehouse_storage_units;

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

CREATE TABLE kingdoms (
  kingdom_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(32) NOT NULL,
  owner uuid NOT NULL,
  CONSTRAINT FK_KINGDOM_USER FOREIGN KEY (owner) REFERENCES users(user_id)
);

CREATE TABLE buildings (
  building_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(100) NOT NULL,
  kingdom_id uuid NOT NULL,
  CONSTRAINT FK_KINGDOM_BUILDING FOREIGN KEY (kingdom_id) REFERENCES kingdoms(kingdom_id)
);

CREATE TABLE warehouses (
  building_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(100) NOT NULL,
  kingdom_id uuid NOT NULL,
  CONSTRAINT FK_KINGDOM_BUILDING FOREIGN KEY (kingdom_id) REFERENCES kingdoms(kingdom_id)
);

CREATE TABLE storage_units (
  building_id uuid NOT NULL,
  resource_id uuid NOT NULL,
  amount INTEGER NOT NULL,
  max_amount INTEGER NOT NULL,
  CONSTRAINT FK_STORAGE_UNIT_BUILDING FOREIGN KEY (building_id) REFERENCES buildings(building_id),
  CONSTRAINT FK_STORAGE_UNIT_RESOURCE FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
);
