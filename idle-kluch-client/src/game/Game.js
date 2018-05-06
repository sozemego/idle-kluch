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
import {
  getKingdomStartingPoint as _getKingdomStartingPoint,
  getSelectedConstructableBuilding as _getSelectedConstructableBuilding,
} from "../kingdom/selectors";
import { COMPONENT_TYPES, IMAGES, MAX_HEIGHT, MAX_WIDTH, TILE_SIZE } from "./constants";
import { OwnershipComponent } from "../ecs/components/OwnershipComponent";
import { NameComponent } from "../ecs/components/NameComponent";
import { BuildableComponent } from "../ecs/components/BuildableComponent";
import { StaticOccupySpaceComponent } from "../ecs/components/StaticOccupySpaceComponent";
import { findComponent } from "../ecs/utils";
import { centerCameraAt } from "./utils";

const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));
const getKingdomStartingPoint = () => _getKingdomStartingPoint(store.getState());

let game = null;
let engine = null;

let selectedBuildingSprite = null;

const tileSprites = {};

const initialState = {
  tiles: {},
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
      if(component.componentType === COMPONENT_TYPES.NAME) {
        return new NameComponent(component.name);
      }
      if(component.componentType === COMPONENT_TYPES.BUILDABLE) {
        return new BuildableComponent();
      }
      if(component.componentType === COMPONENT_TYPES.STATIC_OCCUPY_SPACE) {
        return new StaticOccupySpaceComponent();
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

  const graphicsComponent = findComponent(building, COMPONENT_TYPES.GRAPHICS);

  if (!selectedBuildingSprite) {
    selectedBuildingSprite = game.add.sprite(0, 0, graphicsComponent.asset);
  }

  selectedBuildingSprite.revive();
  selectedBuildingSprite.loadTexture(graphicsComponent.asset);

  return state;
};

export const gameReducer = createReducer(initialState, {
  [ GAME_ACTIONS.ADD_TILES ]: addTiles,
  [ GAME_ACTIONS.ADD_ENTITY ]: addEntity,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setConstructableBuilding,
});

const createGame = () => {
  return new Promise(resolve => {
    let cursors = null;

    // let background = null;

    const preload = function () {
      console.log("preloading!");
      Object.entries(IMAGES).forEach(([asset, filename]) => {
        this.load.image(asset, filename);
      });
    };

    const create = function () {
      console.log("creating!");

      cursors = game.input.keyboard.createCursorKeys();

      game.world.resize(MAX_WIDTH * TILE_SIZE, MAX_HEIGHT * TILE_SIZE);

      centerCameraAt(game, getKingdomStartingPoint());

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
      if (selectedConstructableBuilding && selectedBuildingSprite && selectedBuildingSprite.alive) {
        const physicsComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.PHYSICS);
        selectedBuildingSprite.x = mouseX + x;
        selectedBuildingSprite.y = mouseY + y;
        selectedBuildingSprite.width = physicsComponent.width;
        selectedBuildingSprite.height = physicsComponent.height;
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
