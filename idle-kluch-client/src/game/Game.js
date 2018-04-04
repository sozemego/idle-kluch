import { getTiles as _getTiles } from './selectors';
import { getSelectedConstructableBuilding as _getSelectedConstructableBuilding } from '../kingdom/selectors';
import store from '../store/store';
import Phaser from 'phaser';
import { onCanvasClicked } from './actions';
import { createReducer } from '../store/utils';
import * as GAME_ACTIONS from './actions';

const getTiles = () => _getTiles(store.getState());
const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());

const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));

let game = null;
const tileSprites = {};
const buildingSprites = {};

const initialState = {
  tiles: {},
  buildings: {},
};

const addTiles = (state, { payload: tiles }) => {
  const previousTiles = { ...state.tiles };
  tiles.forEach(tile => {
	const { x, y } = tile;
	const key = `${x}:${y}`;
	const previousTile = previousTiles[key];
	if (!previousTile) {
	  previousTiles[key] = tile;
	  const sprite = game.add.sprite(x * TILE_SIZE, y * TILE_SIZE, 'grass_1');
	  sprite.inputEnabled = true;

	  tileSprites[key] = sprite;
	}
  });
  return { ...state, tiles: previousTiles };
};

const addBuildings = (state, { payload: buildings }) => {
  const previousBuildings = { ...state.tiles };
  buildings.forEach(building => {
	const { buildingId, x, y } = building;
	const previousBuilding = previousBuildings[buildingId];
	if (!previousBuilding) {
	  previousBuildings[buildingId] = building;
	  const sprite = game.add.sprite(x, y, building.asset);
	  sprite.inputEnabled = true;

	  buildingSprites[buildingId] = sprite;
	}
  });
  return { ...state, buildings: previousBuildings };
};

export const gameReducer = createReducer(initialState, {
  [GAME_ACTIONS.ADD_TILES]: addTiles,
  [GAME_ACTIONS.ADD_BUILDINGS]: addBuildings,
});

const TILE_SIZE = 128;

const createGame = () => {
  return new Promise((resolve) => {
	let cursors = null;
	let selectedBuildingSprite = null;

	// let background = null;

	const preload = function () {
	  console.log('preloading!');
	  this.load.image('grass_1', 'grass_1.png');
	  this.load.image('small_warehouse', 'small_warehouse.png');
	  this.load.image('warehouse', 'warehouse.png');
	};

	const create = function () {
	  console.log('creating!');

	  cursors = game.input.keyboard.createCursorKeys();

	  game.world.resize(5000, 5000);
	  game.camera.x = 0;
	  game.camera.y = 0;

	  game.input.onDown.add((pointer) => {
		const x = pointer.x + game.camera.x;
		const y = pointer.y + game.camera.y;
		onCanvasClick(x, y);
	  });

	  return resolve();
	};

	const update = () => {

	  const { x: mouseX, y: mouseY } = game.input;

	  const { x, y } = game.camera;
	  if (cursors.up.isDown) {
		game.camera.y = y - 5;
	  }
	  if (cursors.down.isDown) {
		game.camera.y = y + 5;
	  }
	  if (cursors.left.isDown) {
		game.camera.x = x - 5;
	  }
	  if (cursors.right.isDown) {
		game.camera.x = x + 5;
	  }

	  //show which tile is hovered with mouse
	  Object.values(tileSprites).forEach(tileSprite => {
		tileSprite.tint = 0xffffff;
		if (tileSprite.input.pointerOver()) {
		  tileSprite.tint = (200 << 16) | (200 << 8) | 200;
		}
	  });

	  //TODO make it all reactive
	  if (selectedBuildingSprite) {
		selectedBuildingSprite.kill(true);
	  }

	  //selected building highlight
	  const selectedConstructableBuilding = getSelectedConstructableBuilding();
	  if (selectedConstructableBuilding) {

		//remove previous sprite
		if (selectedBuildingSprite && !selectedConstructableBuilding) {
		  selectedBuildingSprite.kill(true);
		}

		if (!selectedBuildingSprite) {
		  selectedBuildingSprite = game.add.sprite(mouseX - 24, mouseY - 24, selectedConstructableBuilding.asset);
		}

		selectedBuildingSprite.revive();
		selectedBuildingSprite.loadTexture(selectedConstructableBuilding.asset);
		selectedBuildingSprite.x = mouseX + x;
		selectedBuildingSprite.y = mouseY + y;
		selectedBuildingSprite.width = selectedConstructableBuilding.width;
		selectedBuildingSprite.height = selectedConstructableBuilding.height;

	  }

	};

	const render = () => {

	};

	const config = {
	  type: Phaser.CANVAS,
	  parent: 'game',
	  width: TILE_SIZE * 14,
	  height: TILE_SIZE * 6,
	  scene: {
		preload,
		create,
		update,
		render,
	  }
	};

	game = new Phaser.Game(
	  config.width, config.height,
	  config.type, config.parent,
	  {
		...config.scene
	  }
	);

  });
};

export const events = {
  'BUILDING_PLACED': 'BUILDING_PLACED',
};

export default createGame;