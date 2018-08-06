import Phaser from "phaser";
import _ from "lodash";
import { GameService as gameService } from "./GameService";
import { makeActionCreator, makePayloadActionCreator } from "../store/utils";
import createGame from "./Game";
import {
	getConstructableBuildingsData,
	getCost,
	getKingdom,
	getSelectedConstructableBuilding, getUpgrades
} from "../kingdom/selectors";
import { idleBucksChanged, setSelectedConstructableBuilding } from "../kingdom/actions";
import { checkRectangleIntersectsCollidableEntities, doesContain, findComponent } from "../ecs/utils";
import { getAttachSourceSlot, getEngine, getSelectedEntityId } from "./selectors";
import { COMPONENT_TYPES, UPGRADE_TYPE } from "./constants";
import { default as undoActions } from "./UndoActions";
import { ResourceHarvesterComponent } from "../ecs/components/ResourceHarvesterComponent";
import { ResourceSourceComponent } from "../ecs/components/ResourceSourceComponent";
import { getComponentByUpgradeType, getComponentClassByType } from "./utils";
import { ResourceSellerComponent } from "../ecs/components/ResourceSellerComponent";

export const ADD_TILES = "ADD_TILES";
export const addTiles = makePayloadActionCreator(ADD_TILES);

export const ADD_BUILDINGS = "ADD_BUILDINGS";
export const addBuildings = makePayloadActionCreator(ADD_BUILDINGS);

export const ADD_ENTITY = "ADD_ENTITY";
export const addEntity = makePayloadActionCreator(ADD_ENTITY);

export const REMOVE_ENTITY = "REMOVE_ENTITY";
export const removeEntity = makePayloadActionCreator(REMOVE_ENTITY);

export const SET_RUNNING_STATE = "SET_RUNNING_STATE";
export const setRunningState = makePayloadActionCreator(SET_RUNNING_STATE);

export const START_HARVESTING = "START_HARVESTING";
export const startHarvesting = makePayloadActionCreator(START_HARVESTING);

export const START_SELLING = "START_SELLING";
export const startSelling = makePayloadActionCreator(START_SELLING);

export const SET_SELECTED_ENTITY = "SET_SELECTED_ENTITY";
export const setSelectedEntity = makePayloadActionCreator(SET_SELECTED_ENTITY);

export const SET_RESOURCES = "SET_RESOURCES";
export const setResources = makePayloadActionCreator(SET_RESOURCES);

export const SET_ENGINE = "SET_ENGINE";
export const setEngine = makePayloadActionCreator(SET_ENGINE);

export const ON_ATTACH_SOURCE_CLICKED = "ON_ATTACH_SOURCE_CLICKED";
export const onAttachSourceClicked = makePayloadActionCreator(ON_ATTACH_SOURCE_CLICKED);

export const ATTACH_SOURCE = "ATTACH_SOURCE";
export const attachSource = makePayloadActionCreator(ATTACH_SOURCE);

export const TRANSFER_RESOURCE = "TRANSFER_RESOURCE";
export const transferResource = makePayloadActionCreator(TRANSFER_RESOURCE);

export const COMPONENT_CHANGED = "COMPONENT_CHANGED";
export const componentChanged = makeActionCreator(COMPONENT_CHANGED, "payload");

let gameContainer = null;

export const connect = () => {
	return (dispatch, getState) => {
		return dispatch(startGame())
			.then(() => gameService.connect());
	};
};

export const startGame = () => {
	return (dispatch, getState) => {
		return createGame()
			.then(data => {
				dispatch(setEngine(() => data.engine));
				gameContainer = data;
			});
	};
};

export const selectConstructableBuilding = id => {
	return (dispatch, getState) => {
		const constructableBuildings = getConstructableBuildingsData(getState);
		const building = constructableBuildings.find(
			building => building.id === id,
		);
		dispatch(setSelectedConstructableBuilding(building));
	};
};

export const onCanvasClicked = (x, y) => {
	return (dispatch, getState) => {
		console.log("on canvas clicked!", x, y);

		//canvas was clicked, lets check what we can do
		const selectedConstructableBuilding = getSelectedConstructableBuilding(getState);
		if (selectedConstructableBuilding) {
			//check player money
			const kingdom = getKingdom(getState);
			const cost = getCost(getState(), selectedConstructableBuilding);
			if (kingdom.idleBucks < cost) {
				return Promise.resolve();
			}

			//check if doesn't collide with any other entities
			const physicsComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.PHYSICS);
			const bounds = new Phaser.Rectangle(physicsComponent.x, physicsComponent.y, physicsComponent.width, physicsComponent.height);
			const selectedConstructableBuildingCollides = checkRectangleIntersectsCollidableEntities(getEngine(getState), bounds);
			if (selectedConstructableBuildingCollides) {
				return Promise.resolve();
			}

			//send network request to build
			x = x - (physicsComponent.width / 2);
			y = y - (physicsComponent.height / 2);
			const messageId = gameService.constructBuilding(selectedConstructableBuilding.id, x, y);

			undoActions.addAction(messageId, () => {
				dispatch(idleBucksChanged(cost));
			});

			dispatch(idleBucksChanged(-cost));

			return Promise.resolve();
		}

		const engine = gameContainer.engine;
		const entities = engine.getAllEntities();
		const attachSourceSlot = getAttachSourceSlot(getState);

		if (attachSourceSlot) {
			const entityAtMouse = entities.find(entity => doesContain(entity, { x, y })) || null;
			if (entityAtMouse) {
				const selectedEntity = getSelectedEntity(getState);
				const harvester = selectedEntity.getComponent(ResourceHarvesterComponent);
				const harvestedResource = harvester.getResource();
				const source = entityAtMouse.getComponent(ResourceSourceComponent);
				if (source) {
					const sourceResource = source.getResourceId();
					if (harvestedResource === sourceResource) {
						const previousSourceId = harvester.getSlots()[attachSourceSlot - 1].sourceId;
						const messageId = gameService.attachResourceSource(selectedEntity.getId(), entityAtMouse.getId(), attachSourceSlot);
						dispatch(attachSource({ harvesterId: selectedEntity.getId(), sourceId: entityAtMouse.getId(), slot: attachSourceSlot }));
						undoActions.addAction(messageId, () => {
							dispatch(attachSource({ harvesterId: selectedEntity.getId(), sourceId: previousSourceId, slot: attachSourceSlot }));
						});
						dispatch(onAttachSourceClicked(null));
						return Promise.resolve();
					}
				}
			}
		}

		const selectedEntity = entities.find(entity => doesContain(entity, { x, y })) || null;

		console.log(selectedEntity);
		dispatch(setSelectedEntity(_.result(selectedEntity, 'getId', null)));

		return Promise.resolve();
	};
};

const getSelectedEntity = (state) => {
	const selectedEntityId = getSelectedEntityId(state);
	return getEngine(state).getEntityById(selectedEntityId);
};

export const onUpgradeComponentClicked = (entityId, upgradeType, level) => {
	return (dispatch, getState) => {

		const upgrades = getUpgrades(getState);
		const engine = getEngine(getState);

		let field = null;
		let data = null;
		let previousData = null;
		let cost = null;

		const componentType = getComponentByUpgradeType(upgradeType);
		const entity = engine.getEntity(entityId);

		if (componentType === COMPONENT_TYPES.RESOURCE_HARVESTER) {
			const component = entity.getComponent(ResourceHarvesterComponent);
			if (upgradeType === UPGRADE_TYPE.HARVESTER_SPEED) {
				const upgrade = upgrades[ upgradeType ][ level - 1 ];
				cost = upgrade.cost;
				field = "unitsPerMinute";
				previousData = component.getUnitsPerMinute();
				data = Math.floor(previousData * upgrade.data * 100) / 100;
			}
		}

		if (componentType === COMPONENT_TYPES.SELLER) {
			const component = entity.getComponent(ResourceSellerComponent);
			if (upgradeType === UPGRADE_TYPE.SELLING_SPEED) {
				const upgrade = upgrades[ upgradeType ][ level - 1 ];
				cost = upgrade.cost;
				field = "secondsPerUnit";
				previousData = component.getSecondsPerUnit();
				data = Math.floor(previousData * (1 / upgrade.data) * 100) / 100;
			}
		}

		const messageId = gameService.upgradeComponent(entityId, upgradeType);
		dispatch(componentChanged({ entityId, componentType, field, data }));
		dispatch(componentChanged({ entityId, componentType, field: "speedLevel", data: level + 1}));
		dispatch(idleBucksChanged(-cost));
		undoActions.addAction(messageId, () => {
			dispatch(componentChanged({ entityId, componentType, field, previousData }));
			dispatch(componentChanged({ entityId, componentType, field: "speedLevel", data: level}));
			dispatch(idleBucksChanged(cost));
		});

	};
};
