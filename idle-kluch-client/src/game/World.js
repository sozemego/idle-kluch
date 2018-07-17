import { TILE_SIZE } from "./constants";

function World(game) {
	this.game = game;
	this.tiles = [];
	this.tileGroup = game.add.group();
};

/**
 * Adds a given tile.
 * Returns true if this tile was added, false if it was already present.
 * @param tile
 * @returns {boolean}
 */
World.prototype.addTile = (tile) => {
	const { x, y } = tile;
	const key = `${x}:${y}`;
	const previousTile = this.tiles[ key ];
	if (!previousTile) {
		this.tiles[ key ] = tile;
		const sprite = this.tileGroup.create(x * TILE_SIZE, y * TILE_SIZE, "grass_1_outline");
		sprite.width = TILE_SIZE;
		sprite.height = TILE_SIZE;
		sprite.inputEnabled = true;
		sprite.autoCull = true;
		return true;
	}
	return false;
};

export const createWorld = (game) => {
	return new World(game);
};
