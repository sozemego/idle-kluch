import store from "../store/store";
import Phaser from "phaser";
import * as GAME_ACTIONS from "./actions";
import { onCanvasClicked } from "./actions";
import { GameService as gameService } from "./GameService";
import * as APP_ACTIONS from "../app/actions";
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
import { COMPONENT_TYPES, IMAGES, MAX_HEIGHT, MAX_WIDTH, TILE_SIZE, ZOOM_AMOUNT } from "./constants";
import { OwnershipComponent } from "../ecs/components/OwnershipComponent";
import { NameComponent } from "../ecs/components/NameComponent";
import { BuildableComponent } from "../ecs/components/BuildableComponent";
import { StaticOccupySpaceComponent } from "../ecs/components/StaticOccupySpaceComponent";
import { findComponent } from "../ecs/utils";
import { attachSpawnAnimation, centerCameraAt, DIRECTIONS } from "./utils";
import { CostComponent } from "../ecs/components/CostComponent";

const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));
const getKingdomStartingPoint = () => _getKingdomStartingPoint(store.getState());

let game = null;
let engine = null;

let selectedBuildingSprite = null;

let tileGroup = null;
let entitySprites = null;

const initialState = {
  tiles: {},
};

const addTiles = (state, { payload: tiles }) => {
  const previousTiles = { ...state.tiles };
  const newTileSprites = [];
  tiles.forEach((tile, index) => {
    const { x, y } = tile;
    const key = `${x}:${y}`;
    const previousTile = previousTiles[ key ];
    if (!previousTile) {
      previousTiles[ key ] = tile;
      const sprite = tileGroup.create(x * TILE_SIZE, y * TILE_SIZE, "grass_1");
      sprite.inputEnabled = true;
      sprite.autoCull = true;
      newTileSprites.push(sprite);
    }
  });
  attachTileSpawnAnimation(newTileSprites);
  return { ...state, tiles: previousTiles };
};

const addEntity = (state, { payload: entity }) => {
  const newEntity = engine.getEntityFactory().createEntity(entity.id);

  entity.components
    .map(component => {
      const { componentType } = component;
      if (componentType === COMPONENT_TYPES.GRAPHICS) {
        const sprite = entitySprites.create(0, 0, component.asset);
        sprite.inputEnabled = true;
        return new GraphicsComponent(sprite);
      }
      if (componentType === COMPONENT_TYPES.PHYSICS) {
        return new PhysicsComponent(
          component.x,
          component.y,
          component.width,
          component.height,
        );
      }
      if (componentType === COMPONENT_TYPES.OWNERSHIP) {
        return new OwnershipComponent(entity.id);
      }
      if (componentType === COMPONENT_TYPES.NAME) {
        return new NameComponent(component.name);
      }
      if (componentType === COMPONENT_TYPES.BUILDABLE) {
        return new BuildableComponent();
      }
      if (componentType === COMPONENT_TYPES.STATIC_OCCUPY_SPACE) {
        return new StaticOccupySpaceComponent();
      }
      if (componentType === COMPONENT_TYPES.COST) {
        return new CostComponent(component.idleBucks);
      }
      throw new Error("INVALID COMPONENT TYPE");
    })
    .forEach(component => newEntity.addComponent(component));

  const graphicsComponent = newEntity.getComponent(GraphicsComponent);
  if(graphicsComponent !== null) {
    const physicsComponent = newEntity.getComponent(PhysicsComponent);
    graphicsComponent.setSpritePosition(physicsComponent.getX(), physicsComponent.getY());
    attachSpawnAnimation(game, graphicsComponent.getSprite(), DIRECTIONS.DOWN);
  }

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

const logout = (state, action) => {
  if(tileGroup) {
    tileGroup.children.forEach((sprite) => {
      sprite.destroy(true, false);
    });
  }

  gameService.disconnect();

  if (game) {
    game.destroy();
    game = null;
  }

  if (selectedBuildingSprite) {
    selectedBuildingSprite.destroy(true, false);
    selectedBuildingSprite = null;
  }

  if (engine) {
    engine.destroy();
    engine = null;
  }

  return { ...state, tiles: {} };
};

const attachTileSpawnAnimation = (tileSprites) => {
  [...tileSprites]
    .sort((a, b) => b.y - a.y) //so tiles that are lower are spawned first
    .forEach((tile, index) => {
      attachSpawnAnimation(game, tile, DIRECTIONS.UP, index * 2);
    });
}

const attachEntitySpawnAnimation = (entitySprites, delay) => {
  entitySprites.forEach((entitySprite, index) => {
    attachSpawnAnimation(game, entitySprite, DIRECTIONS.DOWN, (index * 2) + delay);
  });
}

export const gameReducer = createReducer(initialState, {
  [ GAME_ACTIONS.ADD_TILES ]: addTiles,
  [ GAME_ACTIONS.ADD_ENTITY ]: addEntity,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setConstructableBuilding,
  [ APP_ACTIONS.LOGOUT ]: logout,
});

const mouseOut = (event) => {
  // tileGroup.children.forEach((sprite) => {
  //   sprite.tint = 0xffffff;
  // });
}

const createGame = () => {
  return new Promise(resolve => {
    let cursors = null;

    // let background = null;

    const preload = function () {
      console.log("preloading!");
      Object.entries(IMAGES).forEach(([ asset, filename ]) => {
        this.load.image(asset, filename);
      });
    };

    const create = function () {
      console.log("creating!");

      cursors = game.input.keyboard.createCursorKeys();

      game.input.keyboard.addCallbacks(this, null, (key) => {
        if(key['key'] === 'l') {
          attachTileSpawnAnimation(tileGroup.children);
          attachEntitySpawnAnimation(entitySprites.children, tileGroup.children.length * 2);
        }
      }, null);

      game.world.setBounds(-MAX_WIDTH * TILE_SIZE, -MAX_HEIGHT * TILE_SIZE, MAX_WIDTH * TILE_SIZE * 2, MAX_HEIGHT * TILE_SIZE * 2);

      centerCameraAt(game, getKingdomStartingPoint());

      game.input.onDown.add(pointer => {
        const x = pointer.worldX * Math.pow(game.camera.scale.x, -1);
        const y = pointer.worldY * Math.pow(game.camera.scale.y, -1);
        onCanvasClick(x, y);
      });

      game.input.mouse.mouseWheelCallback = function (event) {
        const zoomAmount = event.wheelDelta > 0 ? ZOOM_AMOUNT : -ZOOM_AMOUNT;
        game.camera.scale.x += zoomAmount;
        game.camera.scale.y += zoomAmount;
        if(game.camera.scale.x <= 0.01) {
          game.camera.scale.x = 0.02;
        }
        if(game.camera.scale.y <= 0.01) {
          game.camera.scale.y = 0.02;
        }
        if(game.camera.scale.x >= 1) {
          game.camera.scale.x = 1;
        }
        if(game.camera.scale.y >= 1) {
          game.camera.scale.y = 1;
        }
      };

      // game.camera.scale.x = 0.25;
      // game.camera.scale.y = 0.25;

      game.input.mouse.capture = true;

      game.stage.disableVisibilityChange = true;

      game.canvas.addEventListener('mouseout', mouseOut);

      tileGroup = game.add.group();
      entitySprites = game.add.group();

      return resolve(game);
    };

    const update = () => {
      // const { x: mouseX, y: mouseY } = game.input;
      const mouseX = game.input.worldX * Math.pow(game.camera.scale.x, -1);
      const mouseY = game.input.worldY * Math.pow(game.camera.scale.y, -1);

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

      //selected building highlight
      const selectedConstructableBuilding = getSelectedConstructableBuilding();
      if (selectedConstructableBuilding && selectedBuildingSprite && selectedBuildingSprite.alive) {
        const physicsComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.PHYSICS);
        selectedBuildingSprite.x = mouseX - (physicsComponent.width / 2);
        selectedBuildingSprite.y = mouseY - (physicsComponent.height / 2);
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
      width: TILE_SIZE * 10,
      height: TILE_SIZE * 5,
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
