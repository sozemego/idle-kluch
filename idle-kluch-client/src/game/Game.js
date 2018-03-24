import { getTiles } from './selectors';
import store from '../store/store';
import Phaser from 'phaser';

const TILE_SIZE = 128;

const createGame = () => {

  let game = null;
  // let background = null;

  const gameUpdate = () => {

  };

  const config = {
	type: Phaser.CANVAS,
	parent: 'game',
	width: TILE_SIZE * 10,
	height: TILE_SIZE * 6,
	scene: {
	  preload: function() {
		console.log('preloading!')
		this.load.image('grass_1', 'grass_1.png');
	  },
	  create: function() {
		console.log('creating!');

		const tileMap = getTiles(store.getState());
		const tiles = Object.values(tileMap);
		for(let i = 0; i < tiles.length; i++) {
		  const tile = tiles[i];
		  const { x, y } = tile;
		  const sprite = this.add.sprite((x * TILE_SIZE) + (TILE_SIZE / 2), (y * TILE_SIZE) + (TILE_SIZE / 2), 'grass_1');
		}

	  },
	  update: gameUpdate,
	}
  };

  game = new Phaser.Game(config);

  return game;
};

export default createGame;