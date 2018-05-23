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
  checkCanAffordSelectedBuilding as _checkCanAffordSelectedBuilding,
} from "../kingdom/selectors";
import { COMPONENT_TYPES, IMAGES, MAX_HEIGHT, MAX_WIDTH, TILE_SIZE, ZOOM_AMOUNT } from "./constants";
import { OwnershipComponent } from "../ecs/components/OwnershipComponent";
import { NameComponent } from "../ecs/components/NameComponent";
import { BuildableComponent } from "../ecs/components/BuildableComponent";
import { StaticOccupySpaceComponent } from "../ecs/components/StaticOccupySpaceComponent";
import { checkRectangleIntersectsCollidableEntities, findComponent } from "../ecs/utils";
import { attachSpawnAnimation, centerCameraAt, DIRECTIONS } from "./utils";
import { CostComponent } from "../ecs/components/CostComponent";

const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));
const getKingdomStartingPoint = () => _getKingdomStartingPoint(store.getState());
const checkCanAffordSelectedBuilding = () => _checkCanAffordSelectedBuilding(store.getState());

let game = null;
let engine = null;

let selectedBuildingSprite = null;

let tileGroup = null;
let entitySprites = null;

let selectedConstructableBuildingCollides = false;

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
      sprite.width = TILE_SIZE;
      sprite.height = TILE_SIZE;
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
        if(pointer.button === 0) {
          const x = pointer.worldX * Math.pow(game.camera.scale.x, -1);
          const y = pointer.worldY * Math.pow(game.camera.scale.y, -1);
          onCanvasClick(x, y);
        }
      });

      game.input.mouse.mouseWheelCallback = function (event) {
        const zoomAmount = event.wheelDelta > 0 ? ZOOM_AMOUNT : -ZOOM_AMOUNT;

        const scaleTween = game.add.tween(game.world.scale);

        const targetX = Phaser.Math.clamp(game.world.scale.x + zoomAmount, 0.02, 1);
        const targetY = Phaser.Math.clamp(game.world.scale.y + zoomAmount, 0.02, 1);

        scaleTween.to({
          x: targetX,
          y: targetY
        }, 100, Phaser.Easing.Circular.Out);
        scaleTween.start();

      };

      // game.camera.scale.x = 0.25;
      // game.camera.scale.y = 0.25;

      game.input.mouse.capture = true;

      game.stage.disableVisibilityChange = true;

      game.canvas.addEventListener('mouseout', mouseOut);

      tileGroup = game.add.group();
      entitySprites = game.add.group();

      return resolve({
        game,
        engine,
      });
    };

    const update = () => {
      // const { x: mouseX, y: mouseY } = game.input;
      const mouseX = game.input.worldX * Math.pow(game.camera.scale.x, -1);
      const mouseY = game.input.worldY * Math.pow(game.camera.scale.y, -1);

      const scale = game.world.scale.x;

      if (cursors.up.isDown) {
        game.world.pivot.y -= 5 * Math.pow(scale, -1);
      }
      if (cursors.down.isDown) {
        game.world.pivot.y += 5 * Math.pow(scale, -1);
      }
      if (cursors.left.isDown) {
        game.world.pivot.x -= 5 * Math.pow(scale, -1);
      }
      if (cursors.right.isDown) {
        game.world.pivot.x += 5 * Math.pow(scale, -1);
      }

      //selected building highlight
      const selectedConstructableBuilding = getSelectedConstructableBuilding();
      if (selectedConstructableBuilding && selectedBuildingSprite && selectedBuildingSprite.alive) {
        const physicsComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.PHYSICS);
        selectedBuildingSprite.x = mouseX - (physicsComponent.width / 2);
        physicsComponent.x = selectedBuildingSprite.x;
        physicsComponent.y = selectedBuildingSprite.y;
        selectedBuildingSprite.y = mouseY - (physicsComponent.height / 2);
        selectedBuildingSprite.width = physicsComponent.width;
        selectedBuildingSprite.height = physicsComponent.height;

        const canAffordSelectedBuilding = checkCanAffordSelectedBuilding();
        selectedBuildingSprite.alpha = canAffordSelectedBuilding ? 1 : 0.25;

        const selectedConstructableBuildingBounds = new Phaser.Rectangle(
          physicsComponent.x, physicsComponent.y,
          physicsComponent.width, physicsComponent.height
        );

        selectedConstructableBuildingCollides = checkRectangleIntersectsCollidableEntities(engine, selectedConstructableBuildingBounds);
        if(selectedConstructableBuildingCollides) {
          selectedBuildingSprite.tint = 0xff0000;
        } else {
          selectedBuildingSprite.tint = 0xffffff;
        }

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
