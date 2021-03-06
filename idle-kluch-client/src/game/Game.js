import store from "../store/store";
import Phaser from "phaser";
import * as GAME_ACTIONS from "./actions";
import { onCanvasClicked } from "./actions";
import { GameService as gameService } from "./GameService";
import * as APP_ACTIONS from "../app/actions";
import { createReducer, makeSetter } from "../store/utils";
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
import Node from "../ecs/Node";
import {
  getKingdomStartingPoint as _getKingdomStartingPoint,
  getSelectedConstructableBuilding as _getSelectedConstructableBuilding,
  checkCanAffordSelectedBuilding as _checkCanAffordSelectedBuilding,
} from "../kingdom/selectors";
import {
	getTiles as _getTiles,
	getSelectedEntityId as _getSelectedEntityId,
	getResourceById as _getResourceById,
  getAttachSourceSlot as _getAttachSourceSlot,
} from "./selectors";
import { COMPONENT_TYPES, IMAGES, IN_GAME_FONT_SIZE, MAX_HEIGHT, MAX_WIDTH, TILE_SIZE, ZOOM_AMOUNT } from "./constants";
import {
	checkEntityInRangeOfResource,
	checkRectangleIntersectsCollidableEntities,
	findComponent,
	distance,
	getCenter
} from "../ecs/utils";
import {
	attachDespawnAnimation,
	attachSpawnAnimation,
	centerCameraAt, createJumpingSpriteFactory,
	destroyTileGroup,
	DIRECTIONS, drawCircle, drawRect, getComponentClassByType, getRect,
	getWheelDelta,
	killSprite,
	translateCoordinatesToTile,
} from "./utils";
import { ResourceSourceComponent } from "../ecs/components/ResourceSourceComponent";
import { ResourceHarvesterComponent } from "../ecs/components/ResourceHarvesterComponent";
import { ResourceHarvesterSystem } from "../ecs/systems/ResourceHarvesterSystem";
import { ResourceStorageComponent } from "../ecs/components/ResourceStorageComponent";
import { ResourceHarvesterRendererSystem } from "../ecs/systems/ResourceHarvesterRendererSystem";
import { ResourceStorageRendererSystem } from "../ecs/systems/ResourceStorageRendererSystem";
import { EntityNameRendererSystem } from "../ecs/systems/EntityNameRendererSystem";
import { ResourceSellerComponent } from "../ecs/components/ResourceSellerComponent";
import { ResourceSellerSystem } from "../ecs/systems/ResourceSellerSystem";
import { ResourceSellerRendererSystem } from "../ecs/systems/ResourceSellerRendererSystem";
import { ResourceTransportSystem } from "../ecs/systems/ResourceTransportSystem";
import { ResourceTransportRendererSystem } from "../ecs/systems/ResourceTransportRendererSystem";

const getSelectedConstructableBuilding = () => _getSelectedConstructableBuilding(store.getState());
const getSelectedEntityId = () => _getSelectedEntityId(store.getState());
const getTiles = () => _getTiles(store.getState());
const onCanvasClick = (x, y) => store.dispatch(onCanvasClicked(x, y));
const getKingdomStartingPoint = () => _getKingdomStartingPoint(store.getState());
const checkCanAffordSelectedBuilding = () => _checkCanAffordSelectedBuilding(store.getState());
const getResourceById = (id) => _getResourceById(store.getState(), id);
const getAttachSourceSlot = () => _getAttachSourceSlot(store.getState());

let game = null;
let engine = null;

let selectedBuildingSprite = null;
let selectedBuildingRadiusCircle = null;
let entitySelector = null;

let tileGroup = null;
let entitySprites = null;

let isBuildingConstructable = false;

let isRunning = true;

let resourceBonusTextGroup = null;

const delta = 1 / 60;

const initialState = {
  tiles: {},
  selectedEntityId: null,
  resources: [],
  attachSourceSlot: null,
	constructing: false,
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
    .map(createComponent)
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

const createComponent = (component) => {
	const { componentType } = component;
	if (componentType === COMPONENT_TYPES.GRAPHICS) {
		const sprite = entitySprites.create(0, 0, component.asset);
		sprite.inputEnabled = true;
		return new GraphicsComponent(component.asset, sprite);
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
		return new OwnershipComponent(component.ownerId);
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
		return new ResourceSourceComponent(component.resourceId, component.bonus);
	}
	if(componentType === COMPONENT_TYPES.RESOURCE_HARVESTER) {
		return new ResourceHarvesterComponent(
			component.resourceId,
			component.radius,
			component.unitsPerMinute,
			component.slots,
			component.sourceSlots,
			component.harvestingProgress,
			component.speedLevel,
		);
	}
	if(componentType === COMPONENT_TYPES.RESOURCE_STORAGE) {
		const storageComponent = new ResourceStorageComponent(
			component.capacity,
			component.routes,
			component.maxRoutes,
			component.transportSpeed,
			component.transportSpeedLevel,
			component.secondsPerRoute,
			component.nextRouteProgress,
			component.nextRouteTimeLevel,
		);
		component.resources.forEach(resource => storageComponent.addResource(resource));
		return storageComponent;
	}

	if(componentType === COMPONENT_TYPES.SELLER) {
		return new ResourceSellerComponent(
			component.secondsPerUnit,
			component.sellingProgress,
			component.resourceBeingSold,
			component.speedLevel,
		);
	}

	throw new Error("INVALID COMPONENT TYPE " + componentType);
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
		getSelectedBuildingRadiusCircle().clear();
		return { ...state, constructing: false };
	}

	const graphicsComponent = findComponent(building, COMPONENT_TYPES.GRAPHICS);

	if (!selectedBuildingSprite) {
		selectedBuildingSprite = game.add.sprite(0, 0, graphicsComponent.asset);
		selectedBuildingRadiusCircle = getSelectedBuildingRadiusCircle();
	}

	selectedBuildingSprite.revive();
	selectedBuildingSprite.loadTexture(graphicsComponent.asset);

	return { ...state, constructing: true };
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

const startHarvesting = (state, action) => {
  const { payload: id } = action;
  const entity = engine.getEntity(id);
  if(entity == null) {
    return state;
  }
  const harvester = entity.getComponent(ResourceHarvesterComponent);
  harvester.addHarvest();
  return state;
};

const startSelling = (state, action) => {
	const { id, resource } = action.payload;
	const entity = engine.getEntity(id);
	if (entity == null) {
		return state;
	}
	const resourceSeller = entity.getComponent(ResourceSellerComponent);
	resourceSeller.addResourceToQueue(resource);
	return state;
};

const transferResource = (state, action) => {
	const { route } = action.payload;

	const fromEntity = engine.getEntity(route.from);
	const toEntity = engine.getEntity(route.to);

	if (!fromEntity || !toEntity) {
		return state;
	}

	const fromStorage = fromEntity.getComponent(ResourceStorageComponent);
	fromStorage.addRoute(route);
	fromStorage.removeResource(route.resource);
	fromStorage.nextRouteProgress = 0;

	return state;
};

const componentChanged = (state, action) => {
	const { componentType, data, entityId, field } = action.payload;
	const entity = engine.getEntity(entityId);
	const componentClass = getComponentClassByType(componentType);
	const component = entity.getComponent(componentClass);
	component[ field ] = data;
	return state;
};

const setSelectedEntity = (state, action) => {
	return { ...state, selectedEntityId: action.payload, attachSourceSlot: null };
};

const attachSource = (state, action) => {
	const { harvesterId, sourceId, slot } = action.payload;

	const selectedEntity = engine.getEntityById(harvesterId);
	const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
	harvester.setSlot(sourceId, slot);

	return {...state};
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
  [ GAME_ACTIONS.START_HARVESTING ]: startHarvesting,
  [ GAME_ACTIONS.START_SELLING ]: startSelling,
  [ GAME_ACTIONS.SET_SELECTED_ENTITY]: setSelectedEntity,
  [ GAME_ACTIONS.SET_RESOURCES]: makeSetter("resources"),
  [ GAME_ACTIONS.SET_ENGINE]: makeSetter("engine"),
  [ GAME_ACTIONS.ON_ATTACH_SOURCE_CLICKED]: makeSetter("attachSourceSlot"),
  [ GAME_ACTIONS.ATTACH_SOURCE ]: attachSource,
	[ GAME_ACTIONS.TRANSFER_RESOURCE ]: transferResource,
	[ GAME_ACTIONS.COMPONENT_CHANGED ]: componentChanged,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setConstructableBuilding,
  [ APP_ACTIONS.LOGOUT ]: logout,
});

const getSelectedBuildingRadiusCircle = () => {
	if (!selectedBuildingRadiusCircle) {
		selectedBuildingRadiusCircle = game.add.graphics(0, 0);
	}
	return selectedBuildingRadiusCircle;
};

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

    const resourceHarvesterComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.RESOURCE_HARVESTER);
    if(resourceHarvesterComponent != null) {

      const inRangeOfResources = checkEntityInRangeOfResource(engine, selectedConstructableBuilding);
      const color = inRangeOfResources ? 0x00ff00 : 0xff0000;

			drawCircle(
			  selectedBuildingRadiusCircle,
        {
          x: mouseX + game.world.pivot.x,
          y: mouseY + game.world.pivot.y
        },
				resourceHarvesterComponent.radius,
        color,
      );

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

const getSelectedEntity = () => {
	const selectedEntityId = getSelectedEntityId();
	return engine.getEntityById(selectedEntityId);
};

const updateSelectedEntity = () => {
  const selectedEntity = getSelectedEntity();
  if(selectedEntity === null) {
    entitySelector.clear();
  } else {
		drawRect(
			entitySelector,
			getRect(selectedEntity),
			{ width: 2, color: 0xdd00dd, alpha: 1 },
		);
  }
};

const updateAttachingSource = () => {
	destroyTileGroup(resourceBonusTextGroup);

	const attachSourceSlot = getAttachSourceSlot();
  const selectedEntity = getSelectedEntity();
  if (!selectedEntity || !attachSourceSlot) {
    return;
  }

	const resourceHarvester = selectedEntity.getComponent(ResourceHarvesterComponent);
	const radius = resourceHarvester.getRadius();
	const center = getCenter(selectedEntity);

	drawCircle(
		getSelectedBuildingRadiusCircle(),
		center,
		radius,
		0x00ff00,
	);


  const resourceId = resourceHarvester.getResource();

	engine.getEntitiesByNode(Node.of([ PhysicsComponent, ResourceSourceComponent ]))
		.filter(source => {
			const resourceSource = source.getComponent(ResourceSourceComponent);
			return resourceSource.getResourceId() === resourceId;
		})
		.filter(source => {
			return distance(source, selectedEntity) <= radius;
		})
		.forEach(source => {
			const rect = getRect(source);
			drawRect(
				entitySelector,
				rect,
				{ width: 2, color: 0xdd00dd, alpha: 1 },
			);

			const sourceComponent = source.getComponent(ResourceSourceComponent);
			const text = game.make.text(0, 0, "", { font: `${IN_GAME_FONT_SIZE}px Arial`, fill: "rgb(0, 0, 0)" });
			text.text = `${sourceComponent.getBonus()}x`;
			text.x = rect.x;
			text.y = rect.y - IN_GAME_FONT_SIZE;
			resourceBonusTextGroup.add(text);
		});

  //4. show bonuses for resources
};

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
      entitySelector = game.add.graphics(0, 0);
			resourceBonusTextGroup = game.add.group();

      engine = new Engine();
      engine.addSystem(new PhysicsSystem(engine));
      engine.addSystem(new GraphicsSystem(engine));
      engine.addSystem(new ResourceHarvesterSystem(engine, getResourceById));
      engine.addSystem(new ResourceHarvesterRendererSystem(engine, game.add, getSelectedEntity, game.add.graphics(0, 0), createJumpingSpriteFactory(game), getResourceById));
      engine.addSystem(new ResourceSellerSystem(engine));
      engine.addSystem(new ResourceSellerRendererSystem(engine, game.add, getSelectedEntity, game.add.graphics(0, 0), createJumpingSpriteFactory(game)));
      engine.addSystem(new ResourceStorageRendererSystem(engine, game.add.group(), game.make, getSelectedEntity));
			engine.addSystem(new ResourceTransportSystem(engine));
			engine.addSystem(new ResourceTransportRendererSystem(engine, game.add, game.add.graphics(0, 0)));
			engine.addSystem(new EntityNameRendererSystem(engine, game.add.group(), game.make, getSelectedEntity));

      return resolve({
        game,
        engine,
      });
    };

    let accumulator = 0;

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

			getSelectedBuildingRadiusCircle().clear();

      updateSelectedConstructableBuilding();
      updateSelectedEntity();
      updateAttachingSource();

      if(isRunning) {
        accumulator += (game.time.elapsedMS / 1000);

        if(accumulator > 3) {
        	console.log('Would take too long to update, reloading the page ' + accumulator);
        	return window.location.reload();
				}

        while(accumulator >= delta) {
          engine.update(delta);
          accumulator -= delta;
        }
      }
    };

    const render = () => {
			entitySelector.clear();
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

  });
};

export default createGame;
