/**
 * Contains various utility functions which help handle the game itself.
 * Mostly Phaser specific stuff.
 */

import { TILE_SIZE } from "./constants";

/**
 * Centers camera at a given point.
 */
export const centerCameraAt = (game, { x, y }) => {
  game.camera.x = (x * TILE_SIZE) - (game.scale.width / 2);
  game.camera.y = (y * TILE_SIZE) - (game.scale.height / 2);
};