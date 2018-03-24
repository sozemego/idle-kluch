import { getTiles } from './selectors';
import store from '../store/store';
import Phaser from 'phaser';

const TILE_SIZE = 128;

const createGame = () => {

  let game = null;
  let background = null;

  const gameUpdate = () => {

  };

  const config = {
	type: Phaser.CANVAS,
	parent: 'game',
	width: window.innerWidth,
	height: window.innerHeight,
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
		  const sprite = this.add.sprite(x * TILE_SIZE, y * TILE_SIZE, 'grass_1');
		  sprite.setSize(TILE_SIZE, TILE_SIZE, true);
		}

	  },
	  update: gameUpdate,
	}
  };

  game = new Phaser.Game(config);

  return game;
};

export default createGame;