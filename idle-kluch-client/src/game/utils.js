/**
 * Contains various utility functions which help handle the game itself.
 * Mostly Phaser specific stuff.
 */
import Phaser from "phaser";
import _ from 'lodash';

import { TILE_SIZE } from "./constants";

/**
 * Centers camera at a given point.
 */
export const centerCameraAt = (game, { x, y }) => {
  game.world.pivot.setTo(x * TILE_SIZE, y * TILE_SIZE);
};

export const UP = 'UP';
export const DOWN = 'DOWN';

export const DIRECTIONS = {
  UP, DOWN,
};

const tweeningSprites = [];

/**
 * Attaches data required for a spawn animation to this sprite.
 * @param game
 * @param sprite
 * @param direction {string} - specifies whether the sprite should 'drop' from the bottom or go up
 */
export const attachSpawnAnimation = (game, sprite, direction, delay) => {
  const tweeningSpriteIndex = tweeningSprites.findIndex(tweeningSprite => tweeningSprite === sprite);
  if(tweeningSpriteIndex > -1) {
    return;
  }

  tweeningSprites.push(sprite);
  sprite.idleTweening = true;

  const tween = game.add.tween(sprite);

  const originalX = sprite.x;
  const originalY = sprite.y;
  const originalWidth = sprite.width;
  sprite.y = originalY + (Math.random() * sprite.height) * (direction === UP ? 1 : -1);
  sprite.alpha = 0;

  const widthPercentage = sprite.width / originalWidth;
  if(direction === UP) {
    sprite.width = sprite.width / 2;
    sprite.x = originalX + (originalWidth * (widthPercentage / 2));
  }

  tween.to({
    y: originalY,
    x: originalX,
    width: originalWidth,
    alpha: 1,
  }, 750, Phaser.Easing.Circular.Out);

  tween.onComplete.add(() => {
    const tweeningSpriteIndex = tweeningSprites.findIndex(tweeningSprite => tweeningSprite === sprite);
    tweeningSprites[tweeningSpriteIndex].idleTweening = false;
    tweeningSprites.splice(tweeningSpriteIndex, 1);
  });

  setTimeout(() => {
    tween.start();
  }, delay);
};

export const killSprite = _.method('kill', true);
export const destroySprite = _.method('destroy', [true, false]);
export const destroyTileGroup = _.method('children.forEach', destroySprite);

export const getWheelDelta = (event) => event.wheelDelta || -event.deltaY;