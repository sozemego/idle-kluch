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
import { OwnershipComponent } from "../ecs/components/OwnershipComponent";
import { NameComponent } from "../ecs/components/NameComponent";
import { BuildableComponent } from "../ecs/components/BuildableComponent";
import { StaticOccupySpaceComponent } from "../ecs/components/StaticOccupySpaceComponent";
import { CostComponent } from "../ecs/components/CostComponent";
import {
  getKingdomStartingPoint as _getKingdomStartingPoint,
  getSelectedConstructableBuilding as _getSelectedConstructableBuilding,
  checkCanAffordSelectedBuilding as _checkCanAffordSelectedBuilding,
} from "../kingdom/selectors";
import {
  getTiles as _getTiles,
} from "./selectors";
import { COMPONENT_TYPES, IMAGES, MAX_HEIGHT, MAX_WIDTH, TILE_SIZE, ZOOM_AMOUNT } from "./constants";
import { checkEntityInRangeOfResource, checkRectangleIntersectsCollidableEntities, findComponent } from "../ecs/utils";
import {
  attachDespawnAnimation,
  attachSpawnAnimation,
  centerCameraAt,
  destroyTileGroup,
  DIRECTIONS,
  getWheelDelta,
  killSprite,
  translateCoordinatesToTile,
} from "./utils";
import { ResourceSourceComponent } from "../ecs/components/ResourceSourceComponent";
import { ResourceHarvesterComponent } from "../ecs/components/ResourceHarvesterComponent";

const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());
const getTiles = () => _getTiles(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));
const getKingdomStartingPoint = () => _getKingdomStartingPoint(store.getState());
const checkCanAffordSelectedBuilding = () => _checkCanAffordSelectedBuilding(store.getState());

let game = null;
let engine = null;

let selectedBuildingSprite = null;
let selectedBuildingRadiusCircle = null;

let tileGroup = null;
let entitySprites = null;

let isBuildingConstructable = false;

let updateTimeLeft = 0;

let isRunning = true;

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
      const sprite = tileGroup.create(x * TILE_SIZE, y * TILE_SIZE, "grass_1_outline");
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
      if (componentType === COMPONENT_TYPES.RESOURCE_SOURCE) {
        return new ResourceSourceComponent(component.resource);
      }
      if(componentType === COMPONENT_TYPES.RESOURCE_HARVESTER) {
        return new ResourceHarvesterComponent(
          component.resource,
          component.radius,
          component.unitsPerMinute
        );
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

const removeEntity = (state, action) => {
  const { payload: entityId } = action;

  const entity = engine.getEntity(entityId);

  const graphicsComponent = entity.getComponent(GraphicsComponent);
  if(graphicsComponent) {
    const sprite = graphicsComponent.getSprite();
    attachDespawnAnimation(game, sprite, 250);
  }

  engine.removeEntity(entityId);

  return state;
};

const setConstructableBuilding = (state, action) => {
  const { payload: building } = action;

  if (!building) {
    killSprite(selectedBuildingSprite);
    // if (selectedBuildingSprite) {
    //   selectedBuildingSprite.kill(true);
    // }
    return state;
  }

  const graphicsComponent = findComponent(building, COMPONENT_TYPES.GRAPHICS);

  if (!selectedBuildingSprite) {
    selectedBuildingSprite = game.add.sprite(0, 0, graphicsComponent.asset);
    selectedBuildingRadiusCircle = game.add.graphics(0, 0);
  }

  selectedBuildingSprite.revive();
  selectedBuildingSprite.loadTexture(graphicsComponent.asset);

  return state;
};

const logout = (state, action) => {
  destroyTileGroup(tileGroup);

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

const setRunningState = (state, action) => {
  isRunning = action.payload;
  return state;
};

const attachTileSpawnAnimation = (tileSprites) => {
  [...tileSprites]
    .sort((a, b) => b.y - a.y) //so tiles that are lower are spawned first
    .forEach((tile, index) => {
      attachSpawnAnimation(game, tile, DIRECTIONS.UP, index * 2);
    });
};

const attachEntitySpawnAnimation = (entitySprites, delay) => {
  entitySprites.forEach((entitySprite, index) => {
    attachSpawnAnimation(game, entitySprite, DIRECTIONS.DOWN, (index * 2) + delay);
  });
};

export const gameReducer = createReducer(initialState, {
  [ GAME_ACTIONS.ADD_TILES ]: addTiles,
  [ GAME_ACTIONS.ADD_ENTITY ]: addEntity,
  [ GAME_ACTIONS.REMOVE_ENTITY ]: removeEntity,
  [ GAME_ACTIONS.SET_RUNNING_STATE]: setRunningState,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setConstructableBuilding,
  [ APP_ACTIONS.LOGOUT ]: logout,
});

const mouseOut = (event) => {
  // tileGroup.children.forEach((sprite) => {
  //   sprite.tint = 0xffffff;
  // });
}

const togglePause = () => {
  gameService.togglePause();
};

const updateSelectedConstructableBuilding = () => {
  //selected building highlight
  const selectedConstructableBuilding = getSelectedConstructableBuilding();
  if (selectedConstructableBuilding && selectedBuildingSprite && selectedBuildingSprite.alive) {
    const mouseX = game.input.worldX * Math.pow(game.camera.scale.x, -1);
    const mouseY = game.input.worldY * Math.pow(game.camera.scale.y, -1);

    const physicsComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.PHYSICS);
    selectedBuildingSprite.x = mouseX - (physicsComponent.width / 2) + game.world.pivot.x;
    selectedBuildingSprite.y = mouseY - (physicsComponent.height / 2) + game.world.pivot.y;
    physicsComponent.x = selectedBuildingSprite.x;
    physicsComponent.y = selectedBuildingSprite.y;
    selectedBuildingSprite.width = physicsComponent.width;
    selectedBuildingSprite.height = physicsComponent.height;

    const canAffordSelectedBuilding = checkCanAffordSelectedBuilding();
    selectedBuildingSprite.alpha = canAffordSelectedBuilding ? 1 : 0.25;

    const selectedConstructableBuildingBounds = new Phaser.Rectangle(
      physicsComponent.x, physicsComponent.y,
      physicsComponent.width, physicsComponent.height
    );

    //check if the building collides with another entity
    isBuildingConstructable = !checkRectangleIntersectsCollidableEntities(engine, selectedConstructableBuildingBounds);

    //if not, check if it's out of bounds
    if (isBuildingConstructable) {
      const tileKey = translateCoordinatesToTile(physicsComponent.x, physicsComponent.y);
      const tiles = getTiles();
      if(!tiles[tileKey]) {
        isBuildingConstructable = false;
      }
    }

    selectedBuildingRadiusCircle.clear();

    const resourceHarvesterComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.RESOURCE_HARVESTER);
    if(resourceHarvesterComponent != null) {

      const inRangeOfResources = checkEntityInRangeOfResource(engine, selectedConstructableBuilding);

      const color = inRangeOfResources ? 0x00ff00 : 0xff0000;

      selectedBuildingRadiusCircle.beginFill(color, 0.1);
      selectedBuildingRadiusCircle.drawCircle(
        mouseX + game.world.pivot.x,
        mouseY + game.world.pivot.y,
        resourceHarvesterComponent.radius * 2
      );
      selectedBuildingRadiusCircle.endFill();

      if(!inRangeOfResources) {
        isBuildingConstructable = false;
      }
    }

    if(!isBuildingConstructable) {
      selectedBuildingSprite.tint = 0xff0000;
    } else {
      selectedBuildingSprite.tint = 0xffffff;
    }

  }
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
        if(key['key'] === 'p') {
          togglePause();
        }
      }, null);

      game.world.setBounds(-MAX_WIDTH * TILE_SIZE, -MAX_HEIGHT * TILE_SIZE, MAX_WIDTH * TILE_SIZE * 2, MAX_HEIGHT * TILE_SIZE * 2);

      game.camera.x = (game.width * -0.5);
      game.camera.y = (game.height * -0.5);

      centerCameraAt(game, getKingdomStartingPoint());

      game.input.onDown.add(pointer => {
        if(pointer.button === 0) {
          const x = pointer.worldX * Math.pow(game.camera.scale.x, -1) + game.world.pivot.x;
          const y = pointer.worldY * Math.pow(game.camera.scale.y, -1) + game.world.pivot.y;
          onCanvasClick(x, y);
        }
      });

      game.input.mouse.mouseWheelCallback = function (event) {
        const zoomAmount = getWheelDelta(event) > 0 ? ZOOM_AMOUNT : -ZOOM_AMOUNT;

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

      updateSelectedConstructableBuilding();

      if(isRunning) {
        const delta = game.time.physicsElapsed;
        engine.update(delta);
      }
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
