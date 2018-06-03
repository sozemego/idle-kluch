export const COMPONENT_TYPES = {
  GRAPHICS: "GRAPHICS",
  PHYSICS: "PHYSICS",
  OWNERSHIP: "OWNERSHIP",
  NAME: "NAME",
  BUILDABLE: "BUILDABLE",
  STATIC_OCCUPY_SPACE: "STATIC_OCCUPY_SPACE",
  COST: "COST",
  RESOURCE_SOURCE: "RESOURCE_SOURCE",
};

/**
 * Size, in pixels, of one side of the tile.
 * Tiles are squares.
 * @type {number}
 */
export const TILE_SIZE = 128;

/**
 * Maximum width of the game world, in tiles.
 * @type {number}
 */
export const MAX_WIDTH = 2500;

/**
 * Maximum height of the game world, in tiles.
 * @type {number}
 */
export const MAX_HEIGHT = 2500;

export const ZOOM_AMOUNT = 0.125;

export const IMAGES = {
  "grass_1": "grass_1.png",
  "grass_1_outline": "grass_1_outline.png",
  "small_warehouse": "small_warehouse.png",
  "warehouse": "warehouse.png",
  "forest_1": "forest_1.png",
  "forest_2": "forest_2.png",
  "forest_3": "forest_3.png",
  "forest_4": "forest_4.png",
  "woodcutter": "woodcutter.png",
};
