DROP TABLE IF EXISTS kingdoms CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS world;
DROP TABLE IF EXISTS tiles;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS entities CASCADE;
DROP TABLE IF EXISTS physics_components CASCADE;
DROP TABLE IF EXISTS graphics_components CASCADE;
DROP TABLE IF EXISTS ownership_components CASCADE;

CREATE TABLE users (
  user_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  deleted boolean DEFAULT FALSE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  username citext NOT NULL UNIQUE
);

-- there can only be one world --
CREATE TABLE world (
  world_id BIGINT PRIMARY KEY
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

CREATE TABLE entities (
  entity_id uuid PRIMARY KEY
);

CREATE TABLE physics_components (
  entity_id uuid,
  x real NOT NULL,
  y real NOT NULL,
  width real NOT NULL,
  height real NOT NULL,
  CONSTRAINT FK_PHYSICS_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

CREATE TABLE graphics_components (
  entity_id uuid NOT NULL,
  asset varchar NOT NULL,
  CONSTRAINT FK_PHYSICS_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

CREATE TABLE ownership_components (
  entity_id uuid NOT NULL,
  owner_id uuid NOT NULL,
  CONSTRAINT FK_OWNERSHIP_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

