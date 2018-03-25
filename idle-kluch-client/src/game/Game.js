import { getTiles as _getTiles } from './selectors';
import store from '../store/store';
import Phaser from 'phaser';

const getTiles = () => _getTiles(store.getState());

const TILE_SIZE = 128;

const createGame = () => {

  let game = null;
  let camera = null;

  let cursors = null;
  // let background = null;

  const preload = function () {
	console.log('preloading!')
	this.load.image('grass_1', 'grass_1.png');
	// this.camera.bounds = undefined;
  };

  const create = function () {
	console.log('creating!');

	const tileMap = getTiles();
	const tiles = Object.values(tileMap);
	for (let i = 0; i < tiles.length; i++) {
	  const tile = tiles[i];
	  const { x, y } = tile;
	  const sprite = this.add.sprite(x * TILE_SIZE, y * TILE_SIZE, 'grass_1');
	}

	cursors = game.input.keyboard.createCursorKeys();

	game.world.resize(5000, 5000);
	// game.world.setBounds(-2000, -2000, 2000, 2000);
	game.camera.x = 0;
	game.camera.y = 0;
  };

  const update = () => {
	// console.log(game.camera.bounds);
	// console.log(game.world.bounds);
	// game.world.setBounds
	// console.log('rendering')
	// game(game.camera, 32, 32);
	const {x, y} = game.camera;
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

  // Phaser.Camera.shake();

  // camera = new Phaser.Camera(game, 0, 0, 0, config.width, config.height);
  // camera.shake()

  return game;
};

export default createGame;