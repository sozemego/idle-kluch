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
DROP TABLE IF EXISTS cost_components CASCADE;
DROP TABLE IF EXISTS resource_source_components CASCADE;
DROP TABLE IF EXISTS resource_harvester_components CASCADE;

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
  idle_bucks BIGINT NOT NULL DEFAULT 0,
  starting_point_x INTEGER NOT NULL,
  starting_point_y INTEGER NOT NULL,
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

CREATE TABLE cost_components (
  entity_id uuid NOT NULL,
  idle_bucks INTEGER NOT NULL,
  CONSTRAINT FK_COST_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id)
);

CREATE TABLE resource_source_components (
  entity_id uuid NOT NULL,
  resource_id uuid NOT NULL,
  CONSTRAINT FK_RESOURCE_SOURCE_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id),
  CONSTRAINT FK_RESOURCE_SOURCE_RESOURCE FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
);

CREATE TABLE resource_harvester_components (
  entity_id uuid NOT NULL,
  resource_id uuid NOT NULL,
  radius float NOT NULL,
  units_per_minute INT NOT NULL,
  CONSTRAINT FK_RESOURCE_HARVESTER_ENTITY FOREIGN KEY (entity_id) REFERENCES entities(entity_id),
  CONSTRAINT FK_RESOURCE_HARVESTER_RESOURCE FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
);

-- here entity templates live --
INSERT INTO entities VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', true),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', true),
  ('f520432b-4bf5-448f-95f4-14643e078288', true),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', true)
;

INSERT INTO physics_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 0, 0, 50, 55),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 0, 0, 80, 81),
  ('f520432b-4bf5-448f-95f4-14643e078288', 0, 0, 64, 64),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', 0, 0, 80, 81)
;

INSERT INTO graphics_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 'small_warehouse'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 'warehouse'),
  ('f520432b-4bf5-448f-95f4-14643e078288', 'forest_1'),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', 'woodcutter')
;

INSERT INTO ownership_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', '7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', '4517e8b9-de2e-473d-98e8-4c6c73c46c4d'),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', '7e10d339-dc10-4204-914c-cdfb2039460d')
;

INSERT INTO static_occupy_space_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d'),
  ('f520432b-4bf5-448f-95f4-14643e078288'),
  ('7e10d339-dc10-4204-914c-cdfb2039460d')
;

INSERT INTO name_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 'Small warehouse'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 'Warehouse'),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', 'Woodcutter'),
  ('f520432b-4bf5-448f-95f4-14643e078288', 'forest_1')
;

INSERT INTO buildable_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb'),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d'),
  ('7e10d339-dc10-4204-914c-cdfb2039460d')
;

INSERT INTO cost_components VALUES
  ('7a4df465-b4c3-4e9f-854a-248988220dfb', 100),
  ('4517e8b9-de2e-473d-98e8-4c6c73c46c4d', 250),
  ('7e10d339-dc10-4204-914c-cdfb2039460d', 125)
;

INSERT INTO resource_harvester_components(entity_id, resource_id, radius, units_per_minute)
SELECT '7e10d339-dc10-4204-914c-cdfb2039460d', resource_id, 256, 10
FROM resources WHERE name = 'Wood';

INSERT INTO resource_source_components(entity_id, resource_id)
SELECT 'f520432b-4bf5-448f-95f4-14643e078288', resource_id
FROM resources WHERE name = 'Wood';

