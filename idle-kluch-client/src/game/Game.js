import { getTiles as _getTiles} from './selectors';
import store from '../store/store';
import Phaser from 'phaser';

const getTiles = () => _getTiles(store.getState());

const TILE_SIZE = 128;

const createGame = () => {

  let game = null;
  // let background = null;

  const gameUpdate = () => {
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
	  preload: function() {
		console.log('preloading!')
		this.load.image('grass_1', 'grass_1.png');
	  },
	  create: function() {
		console.log('creating!');

		const tileMap = getTiles();
		const tiles = Object.values(tileMap);
		for(let i = 0; i < tiles.length; i++) {
		  const tile = tiles[i];
		  const { x, y } = tile;
		  const sprite = this.add.sprite(x * TILE_SIZE, y * TILE_SIZE, 'grass_1');
		}

	  },
	  update: gameUpdate,
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