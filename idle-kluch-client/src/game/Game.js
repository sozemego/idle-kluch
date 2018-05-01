import store from "../store/store";
import Phaser from "phaser";
import * as GAME_ACTIONS from "./actions";
import { onCanvasClicked } from "./actions";
import { createReducer } from "../store/utils";
import * as KINGDOM_ACTIONS from "../kingdom/actions";
import { Engine } from "../ecs/Engine";
import { GraphicsComponent } from "../ecs/components/GraphicsComponent";
import { PhysicsComponent } from "../ecs/components/PhysicsComponent";
import { PhysicsSystem } from "../ecs/systems/PhysicsSystem";
import { GraphicsSystem } from "../ecs/systems/GraphicsSystem";
import { getSelectedConstructableBuilding as _getSelectedConstructableBuilding } from "../kingdom/selectors";
import { COMPONENT_TYPES } from "./constants";
import { OwnershipComponent } from "../ecs/components/OwnershipComponent";

const getSelectedConstructableBuilding = () =>
  _getSelectedConstructableBuilding(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));

let game = null;
let engine = null;

let selectedBuildingSprite = null;

const tileSprites = {};

const initialState = {
  tiles: {},
  buildings: {},
};

const addTiles = (state, { payload: tiles }) => {
  const previousTiles = { ...state.tiles };
  tiles.forEach(tile => {
    const { x, y } = tile;
    const key = `${x}:${y}`;
    const previousTile = previousTiles[ key ];
    if (!previousTile) {
      previousTiles[ key ] = tile;
      const sprite = game.add.sprite(x * TILE_SIZE, y * TILE_SIZE, "grass_1");
      sprite.inputEnabled = true;

      tileSprites[ key ] = sprite;
    }
  });
  return { ...state, tiles: previousTiles };
};

const addEntity = (state, { payload: entity }) => {
  const newEntity = engine.getEntityFactory().createEntity(entity.id);

  entity.components
    .map(component => {
      if (component.componentType === COMPONENT_TYPES.GRAPHICS) {
        const sprite = game.add.sprite(0, 0, component.asset);
        sprite.inputEnabled = true;
        return new GraphicsComponent(sprite);
      }
      if (component.componentType === COMPONENT_TYPES.PHYSICS) {
        return new PhysicsComponent(
          component.x,
          component.y,
          component.width,
          component.height,
        );
      }
      if (component.componentType === COMPONENT_TYPES.OWNERSHIP) {
        return new OwnershipComponent(entity.id);
      }
      throw new Error("INVALID COMPONENT TYPE");
    })
    .forEach(component => newEntity.addComponent(component));

  engine.addEntity(newEntity);

  return { ...state };
};

const setConstructableBuilding = (state, action) => {
  const { payload: building } = action;
  if (!building && selectedBuildingSprite) {
    selectedBuildingSprite.kill(true);
    return state;
  }

  if (!selectedBuildingSprite) {
    selectedBuildingSprite = game.add.sprite(0, 0, building.asset);
  }

  selectedBuildingSprite.revive();
  selectedBuildingSprite.loadTexture(building.asset);

  return state;
};

export const gameReducer = createReducer(initialState, {
  [ GAME_ACTIONS.ADD_TILES ]: addTiles,
  [ GAME_ACTIONS.ADD_ENTITY ]: addEntity,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setConstructableBuilding,
});

const TILE_SIZE = 128;

const createGame = () => {
  return new Promise(resolve => {
    let cursors = null;

    // let background = null;

    const preload = function () {
      console.log("preloading!");
      this.load.image("grass_1", "grass_1.png");
      this.load.image("small_warehouse", "small_warehouse.png");
      this.load.image("warehouse", "warehouse.png");
      this.load.image("forest_1", "forest_1.png");
      this.load.image("forest_2", "forest_2.png");
      this.load.image("forest_3", "forest_3.png");
      this.load.image("forest_4", "forest_4.png");
      this.load.image("woodcutter", "woodcutter.png");
    };

    const create = function () {
      console.log("creating!");

      cursors = game.input.keyboard.createCursorKeys();

      game.world.resize(5000, 5000);
      game.camera.x = 0;
      game.camera.y = 0;

      game.input.onDown.add(pointer => {
        const x = pointer.x + game.camera.x;
        const y = pointer.y + game.camera.y;
        onCanvasClick(x, y);
      });

      game.stage.disableVisibilityChange = true;

      return resolve(game);
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

      //selected building highlight
      const selectedConstructableBuilding = getSelectedConstructableBuilding();
      if (
        selectedConstructableBuilding &&
        selectedBuildingSprite &&
        selectedBuildingSprite.alive
      ) {
        selectedBuildingSprite.x = mouseX + x;
        selectedBuildingSprite.y = mouseY + y;
        selectedBuildingSprite.width = selectedConstructableBuilding.width;
        selectedBuildingSprite.height = selectedConstructableBuilding.height;
      }

      engine.update(game.time.physicsElapsed);
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
      },
    };

    game = new Phaser.Game(
      config.width,
      config.height,
      config.type,
      config.parent,
      {
        ...config.scene,
      },
    );

    engine = new Engine();
    engine.addSystem(new PhysicsSystem(engine));
    engine.addSystem(new GraphicsSystem(engine));
  });
};

export default createGame;
