/**
 * Contains various utility functions which help handle the game itself.
 * Mostly Phaser specific stuff.
 */
import Phaser from "phaser";
import _ from 'lodash';

import { COMPONENT_TYPES, IN_GAME_FONT_SIZE, TILE_SIZE, UPGRADE_TYPE } from "./constants";
import { PhysicsComponent } from "../ecs/components/PhysicsComponent";
import { ResourceHarvesterComponent } from "../ecs/components/ResourceHarvesterComponent";
import { ResourceSellerComponent } from "../ecs/components/ResourceSellerComponent";
import { ResourceStorageComponent } from "../ecs/components/ResourceStorageComponent";

/**
 * Centers camera at a given point.
 */
export const centerCameraAt = (game, { x, y }) => {
  const nextX = x * TILE_SIZE + (TILE_SIZE / 2);
  const nextY = y * TILE_SIZE + (TILE_SIZE / 2);
  game.world.pivot.setTo(nextX, nextY);
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

export const attachDespawnAnimation = (game, sprite, delay = 0) => {
  const tween = game.add.tween(sprite);
  tween.to({
    x: sprite.x + (sprite.width / 2),
    width: 0,
    height: 0,
    alpha: 0,
  }, 750, Phaser.Easing.Circular.Out);

  setTimeout(() => {
    tween.start();
  }, delay);

  tween.onComplete.add(() => {
    killSprite(sprite);
  });
};

export const killSprite = _.method('kill', true);
export const destroySprite = _.method('destroy', [true, false]);
export const destroyTileGroup = _.method('children.forEach', destroySprite);

export const getWheelDelta = (event) => event.wheelDelta || -event.deltaY;

export const translateCoordinatesToTile = (x, y) => `${Math.floor(x / TILE_SIZE)}:${Math.floor(y / TILE_SIZE)}`;

export const drawCircle = (graphics, center, radius, color) => {
	graphics.beginFill(color, 0.1);
	graphics.drawCircle(center.x, center.y, radius * 2);
	graphics.endFill();
};

export const drawRect = (graphics, rect, style) => {
	graphics.lineStyle(style.width, style.color, style.alpha);
	graphics.drawRect(
		rect.x, rect.y,
		rect.width, rect.height,
	);
};

export const getRect = (entity) => {
  const physicsComponent = entity.getComponent(PhysicsComponent);
  return {
    x: physicsComponent.getX(),
    y: physicsComponent.getY(),
    width: physicsComponent.getWidth(),
    height: physicsComponent.getHeight(),
  };
};

let texts = null;

export const createJumpingSpriteFactory = (game) => (name, point, text) => spawnJumpingSprite(game, name, point, text);

export const spawnJumpingSprite = (game, name, point, text = "") => {
	if (!texts) {
		texts = game.add.group();
	}

  const sprite = game.add.sprite(point.x, point.y, name);
	const tweenTime = 1500;

  sprite.x = sprite.x - 16;
  sprite.width = 32;
  sprite.height = 32;
	const tween = game.add.tween(sprite);

	tween.to({
    y: point.y - 32,
		alpha: 0,
	}, tweenTime, Phaser.Easing.Circular.Out);

	if (text) {
		const textObject = game.make.text(point.x + 32, point.y, text, { font: `${IN_GAME_FONT_SIZE}px Arial`, fill: "rgb(255, 255, 255)" });
		textObject.x = point.x + 16;
		textObject.y = point.y + (IN_GAME_FONT_SIZE / 2);
		texts.add(textObject);

		const textTween = game.add.tween(textObject);
		textTween.to({
			y: point.y - 32,
			alpha: 0,
		}, tweenTime, Phaser.Easing.Circular.Out);
		textTween.start();
	}

	tween.start();

	tween.onComplete.add(() => {
		killSprite(sprite);
		killSprite(text);
	});
};

export const getComponentClassByType = (componentType) => {
	if (componentType === COMPONENT_TYPES.RESOURCE_HARVESTER) {
		return ResourceHarvesterComponent;
	}
	if (componentType === COMPONENT_TYPES.SELLER) {
		return ResourceSellerComponent;
	}
	if (componentType === COMPONENT_TYPES.RESOURCE_STORAGE) {
		return ResourceStorageComponent;
	}
};

export const getComponentByUpgradeType = (upgradeType) => {
	if (upgradeType === UPGRADE_TYPE.HARVESTER_SPEED) {
		return COMPONENT_TYPES.RESOURCE_HARVESTER;
	}
	if (upgradeType === UPGRADE_TYPE.SELLING_SPEED) {
		return COMPONENT_TYPES.SELLER;
	}
	if (upgradeType === UPGRADE_TYPE.TRANSPORT_SPEED) {
		return COMPONENT_TYPES.RESOURCE_STORAGE;
	}
	if (upgradeType === UPGRADE_TYPE.NEXT_ROUTE_TIME) {
		return COMPONENT_TYPES.RESOURCE_STORAGE;
	}
};