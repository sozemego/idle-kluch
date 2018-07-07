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
DROP TABLE IF EXISTS resource_harvester_slots CASCADE;
DROP TABLE IF EXISTS resource_source_components CASCADE;
DROP TABLE IF EXISTS resource_harvester_components CASCADE;
DROP TABLE IF EXISTS resource_storage_components CASCADE;
DROP TABLE IF EXISTS resource_storage CASCADE;

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
  ('771d49e6-3fd1-436d-88aa-02b5377ba29e', 'Wood'),
  ('b7bdf2b9-e129-437d-bdde-00e8964fc173', 'Stone'),
  ('510bcc29-ec8d-4465-80eb-7f826faba019', 'Plank'),
  ('7d747f18-6805-4364-bd2b-2eb7b717cd20', 'Brick');

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
  resource_storage_component jsonb
);

-- here entity templates live --

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '7a4df465-b4c3-4e9f-854a-248988220dfb', true,
    '{"x": 0, "y": 0, "width": 50, "height": 55, "type": "PHYSICS"}',
    '{"asset": "small_warehouse", "type": "GRAPHICS"}',
    '{"ownerId": "7a4df465-b4c3-4e9f-854a-248988220dfb", "type": "OWNERSHIP"}',
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "Small warehouse", "type": "NAME"}',
    '{"type": "BUILDABLE"}',
    '{"idleBucks": 100, "type": "COST"}',
    null,
    '{"capacity": 100, "resources": [], "type": "RESOURCE_STORAGE"}',
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '4517e8b9-de2e-473d-98e8-4c6c73c46c4d', true,
    '{"x": 0, "y": 0, "width": 80, "height": 81, "type": "PHYSICS"}',
    '{"asset": "warehouse", "type": "GRAPHICS"}',
    '{"ownerId": "4517e8b9-de2e-473d-98e8-4c6c73c46c4d", "type": "OWNERSHIP"}',
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "Warehouse", "type": "NAME"}',
    '{"type": "BUILDABLE"}',
    '{"idleBucks": 250, "type": "COST"}',
    null,
    '{"capacity": 250, "resources": [], "type": "RESOURCE_STORAGE"}',
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    'f520432b-4bf5-448f-95f4-14643e078288', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64, "type": "PHYSICS"}',
    '{"asset": "forest_1", "type": "GRAPHICS"}',
    null,
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "forest_1", "type": "NAME"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.2, "type": "RESOURCE_SOURCE"}',
    null,
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '7e10d339-dc10-4204-914c-cdfb2039460d', true,
    '{"x": 0, "y": 0, "width": 80, "height": 81, "type": "PHYSICS"}',
    '{"asset": "forest_2", "type": "GRAPHICS"}',
    null,
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "forest_2", "type": "NAME"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.0, "type": "RESOURCE_SOURCE"}',
    null,
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '24eba6e6-fe5f-4d14-86a4-cd80331beedf', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64, "type": "PHYSICS"}',
    '{"asset": "forest_3", "type": "GRAPHICS"}',
    null,
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "forest_3", "type": "NAME"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.2, "type": "RESOURCE_SOURCE"}',
    null,
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    'f0e8b54b-3944-41c8-a6b0-ac291c455cd0', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64, "type": "PHYSICS"}',
    '{"asset": "forest_4", "type": "GRAPHICS"}',
    null,
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "forest_4", "type": "NAME"}',
    null,
    null,
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "bonus": 1.0, "type": "RESOURCE_SOURCE"}',
    null,
    null
  )
;

INSERT INTO entities (
  entity_id, template, physics_component, graphics_component, ownership_component, static_occupy_space_component,
  name_component, buildable_component, cost_component, resource_source_component, resource_storage_component,
  resource_harvester_component
)
 VALUES
  (
    '03c99070-66d5-4dea-b57a-39b4f308a505', true,
    '{"x": 0, "y": 0, "width": 64, "height": 64, "type": "PHYSICS"}',
    '{"asset": "woodcutter", "type": "GRAPHICS"}',
    '{"ownerId": "4517e8b9-de2e-473d-98e8-4c6c73c46c4d", "type": "OWNERSHIP"}',
    '{"type": "STATIC_OCCUPY_SPACE"}',
    '{"name": "Woodcutter", "type": "NAME"}',
    '{"type": "BUILDABLE"}',
    '{"idleBucks": 125, "type": "COST"}',
    null,
    '{"capacity": 20, "resources": [], "type": "RESOURCE_STORAGE"}',
    '{"resourceId": "771d49e6-3fd1-436d-88aa-02b5377ba29e", "radius": 256, "unitsPerMinute": 5, "sourceSlots": 0, "sources": [], "type": "RESOURCE_HARVESTER"}'
  )
;
