DROP TABLE IF EXISTS kingdom CASCADE;
DROP TABLE IF EXISTS user_table CASCADE; -- user is postgresql reserved word, so table is called user_table
DROP TABLE IF EXISTS world;
DROP TABLE IF EXISTS tile;
DROP TABLE IF EXISTS resource CASCADE;
DROP TABLE IF EXISTS entity CASCADE;
DROP TABLE IF EXISTS upgrades CASCADE;

CREATE TABLE user_table (
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

CREATE TABLE tile (
  x INTEGER NOT NULL,
  y INTEGER NOT NULL,
  PRIMARY KEY (x, y)
);

CREATE TABLE resource (
  resource_id uuid NOT NULL PRIMARY KEY,
  name VARCHAR(32) NOT NULL UNIQUE,
  price INT NOT NULL
);

INSERT INTO resource VALUES
  ('771d49e6-3fd1-436d-88aa-02b5377ba29e', 'Wood', 10),
  ('b7bdf2b9-e129-437d-bdde-00e8964fc173', 'Stone', 10),
  ('510bcc29-ec8d-4465-80eb-7f826faba019', 'Plank', 45),
  ('7d747f18-6805-4364-bd2b-2eb7b717cd20', 'Brick', 55);

CREATE TABLE kingdom (
  kingdom_id uuid NOT NULL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(32) NOT NULL,
  owner uuid NOT NULL,
  idle_bucks BIGINT NOT NULL DEFAULT 0,
  starting_point_x INTEGER NOT NULL,
  starting_point_y INTEGER NOT NULL,
  CONSTRAINT FK_KINGDOM_USER FOREIGN KEY (owner) REFERENCES user_table(user_id)
);

CREATE TABLE entity (
  entity_id uuid PRIMARY KEY,
  template BOOLEAN NOT NULL DEFAULT false,
  physics_component jsonb,
  graphics_component jsonb,
  ownership_component jsonb,
  static_occupy_space_component jsonb,
  name_component jsonb,
  buildable_component jsonb,
  cost_component jsonb,
  resource_source_component jsonb,
  resource_harvester_component jsonb,
  resource_storage_component jsonb,
  resource_seller_component jsonb
);

-- here entity templates live --

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '7a4df465-b4c3-4e9f-854a-248988220dfb', true,
    '{"x": 0, "y": 0, "width": 50, "height": 55}',
    '{"asset": "small_warehouse"}',
    '{"ownerId": "7a4df465-b4c3-4e9f-854a-248988220dfb"}',
    '{}',
    '{"name": "Small warehouse"}',
    '{}',
    '{"idleBucks": 100}',
    null,
    '{"capacity": 100, "resources": [], "transportSpeed": 30, "secondsPerRoute": 10}',
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '4517e8b9-de2e-473d-98e8-4c6c73c46c4d', true,
    '{"x": 0, "y": 0, "width": 80, "height": 81}',
    '{"asset": "warehouse"}',
    '{"ownerId": "4517e8b9-de2e-473d-98e8-4c6c73c46c4d"}',
    '{}',
    '{"name": "Warehouse"}',
    '{}',
    '{"idleBucks": 250}',
    null,
    '{"capacity": 250, "resources": [], "transportSpeed": 30, "secondsPerRoute": 10}',
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    'f520432b-4bf5-448f-95f4-14643e078288', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64}',
    '{"asset": "forest_1"}',
    null,
    '{}',
    '{"name": "forest_1"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.2}',
    null,
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '7e10d339-dc10-4204-914c-cdfb2039460d', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64}',
    '{"asset": "forest_2"}',
    null,
    '{}',
    '{"name": "forest_2"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.0}',
    null,
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '24eba6e6-fe5f-4d14-86a4-cd80331beedf', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64}',
    '{"asset": "forest_3"}',
    null,
    '{}',
    '{"name": "forest_3"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.2}',
    null,
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    'f0e8b54b-3944-41c8-a6b0-ac291c455cd0', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64}',
    '{"asset": "forest_4"}',
    null,
    '{}',
    '{"name": "forest_4"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.0}',
    null,
    null
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '03c99070-66d5-4dea-b57a-39b4f308a505', true,
    '{"x": 0, "y": 0, "width": 80, "height": 81}',
    '{"asset": "woodcutter"}',
    '{"ownerId": "4517e8b9-de2e-473d-98e8-4c6c73c46c4d"}',
    '{}',
    '{"name": "Woodcutter"}',
    '{}',
    '{"idleBucks": 125}',
    null,
    '{"capacity": 20, "resources": [], "transportSpeed": 30, "secondsPerRoute": 10}',
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "radius": 256, "unitsPerMinute": 5, "sourceSlots": 1, "slots": []}'
  )
;

INSERT INTO entity (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_storage_component, resource_seller_component
)
 VALUES
  (
    '5f3a49ad-4283-4fc2-bcf2-c4d1a0c020fa', true,
    '{"x": 0, "y": 0, "width": 94, "height": 84}',
    '{"asset": "trading_post"}',
    '{"ownerId": "4517e8b9-de2e-473d-98e8-4c6c73c46c4d"}',
    '{}',
    '{"name": "Trading Post"}',
    '{}',
    '{"idleBucks": 375}',
    '{"capacity": 50, "resources": [], "transportSpeed": 30, "secondsPerRoute": 10}',
    '{"secondsPerUnit": 6}'
  )
;

CREATE TABLE upgrades (
  id SERIAL PRIMARY KEY,
  entity_id UUID NOT NULL,
  upgrade_type VARCHAR(32) NOT NULL,
  level INT NOT NULL
);
