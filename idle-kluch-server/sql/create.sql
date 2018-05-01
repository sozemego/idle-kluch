DROP TABLE IF EXISTS kingdoms CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS world;
DROP TABLE IF EXISTS tiles;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS entities CASCADE;
DROP TABLE IF EXISTS physics_components CASCADE;
DROP TABLE IF EXISTS graphics_components CASCADE;
DROP TABLE IF EXISTS ownership_components CASCADE;
DROP TABLE IF EXISTS static_occupy_space_components CASCADE;
DROP TABLE IF EXISTS name_components CASCADE;
DROP TABLE IF EXISTS buildable_components CASCADE;

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
  entity_id uuid PRIMARY KEY,
  template BOOLEAN NOT NULL DEFAULT false
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

CREATE TABLE static_occupy_space_components (
  entity_id uuid NOT NULL,
  CONSTRAINT FK_STATIC_OCCUPY_SPACE_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

CREATE TABLE name_components (
  entity_id uuid NOT NULL,
  name VARCHAR(64) NOT NULL,
  CONSTRAINT FK_NAME_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

CREATE TABLE buildable_components (
  entity_id uuid NOT NULL,
  CONSTRAINT FK_BUILDABLE_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

-- here entity templates exist --
INSERT INTO entities VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', true),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', true)
;

INSERT INTO physics_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 0, 0, 50, 55),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 0, 0, 80, 81)
;

INSERT INTO graphics_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 'small_warehouse'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 'warehouse')
;

INSERT INTO ownership_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', '7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', '4517e8b9-de2e-473d-98e8-4c6c73c46c4d')
;

INSERT INTO static_occupy_space_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d')
;

INSERT INTO name_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 'Small warehouse'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 'Warehouse')
;

INSERT INTO buildable_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d')
;