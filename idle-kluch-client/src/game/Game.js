import { getTiles as _getTiles } from "./selectors";
import store from "../store/store";
import Phaser from "phaser";

const getTiles = () => _getTiles(store.getState());

const TILE_SIZE = 128;

const createGame = () => {

	let game = null;
	let cursors = null;

	const tileSprites = {};
	// let background = null;

	const preload = function () {
		console.log("preloading!");
		this.load.image("grass_1", "grass_1.png");
	};

	const create = function () {
		console.log("creating!");

		const tileMap = getTiles();
		const tiles = Object.values(tileMap);
		for (let i = 0; i < tiles.length; i++) {
			const tile = tiles[i];
			const { x, y } = tile;
			const sprite = this.add.sprite(x * TILE_SIZE, y * TILE_SIZE, "grass_1");
			sprite.inputEnabled = true;

			tileSprites[`${x}:${y}`] = sprite;
		}

		cursors = game.input.keyboard.createCursorKeys();

		game.world.resize(5000, 5000);
		game.camera.x = 0;
		game.camera.y = 0;
	};

	const update = () => {

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

	};

	const render = () => {

	};

	const config = {
		type: Phaser.CANVAS,
		parent: "game",
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