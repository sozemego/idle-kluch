import { getTiles as _getTiles} from './selectors';
import store from '../store/store';
import Phaser from 'phaser';

const getTiles = () => _getTiles(store.getState());

const TILE_SIZE = 128;

const createGame = () => {

  let game = null;
  // let background = null;

  const preload = function() {
	console.log('preloading!')
	this.load.image('grass_1', 'grass_1.png');
  };

  const create = function() {
	console.log('creating!');

	const tileMap = getTiles();
	const tiles = Object.values(tileMap);
	for(let i = 0; i < tiles.length; i++) {
	  const tile = tiles[i];
	  const { x, y } = tile;
	  const sprite = this.add.sprite(x * TILE_SIZE, y * TILE_SIZE, 'grass_1');
	}
  };

  const update = () => {
	// game.world.setBounds
	// console.log('rendering')
	// game(game.camera, 32, 32);
  };

  const render = () => {

  };


  const config = {
	type: Phaser.CANVAS,
	parent: 'game',
	width: TILE_SIZE * 10,
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

  return game;
};

export default createGame;