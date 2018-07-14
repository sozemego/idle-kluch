import Phaser from "phaser";
import _ from "lodash";
import { GameService as gameService } from "./GameService";
import { makeActionCreator } from "../store/utils";
import createGame from "./Game";
import { getConstructableBuildingsData, getKingdom, getSelectedConstructableBuilding } from "../kingdom/selectors";
import { idleBucksChanged, setSelectedConstructableBuilding } from "../kingdom/actions";
import { checkRectangleIntersectsCollidableEntities, doesContain, findComponent } from "../ecs/utils";
import { getEngine } from "./selectors";
import { COMPONENT_TYPES } from "./constants";
import { default as undoActions } from "./UndoActions";

export const ADD_TILES = "ADD_TILES";
export const addTiles = makeActionCreator(ADD_TILES, "payload");

export const ADD_BUILDINGS = "ADD_BUILDINGS";
export const addBuildings = makeActionCreator(ADD_BUILDINGS, "payload");

export const ADD_ENTITY = "ADD_ENTITY";
export const addEntity = makeActionCreator(ADD_ENTITY, "payload");

export const REMOVE_ENTITY = "REMOVE_ENTITY";
export const removeEntity = makeActionCreator(REMOVE_ENTITY, "payload");

export const SET_RUNNING_STATE = "SET_RUNNING_STATE";
export const setRunningState = makeActionCreator(SET_RUNNING_STATE, "payload");

export const START_HARVESTING = "START_HARVESTING";
export const startHarvesting = makeActionCreator(START_HARVESTING, "payload");

export const SET_SELECTED_ENTITY = "SET_SELECTED_ENTITY";
export const setSelectedEntity = makeActionCreator(SET_SELECTED_ENTITY, "payload");

export const SET_RESOURCES = "SET_RESOURCES";
export const setResources = makeActionCreator(SET_RESOURCES, "payload");

export const SET_ENGINE = "SET_ENGINE";
export const setEngine = makeActionCreator(SET_ENGINE, "payload");

export const ON_ATTACH_SOURCE_CLICKED = "ON_ATTACH_SOURCE_CLICKED";
export const onAttachSourceClicked = makeActionCreator(ON_ATTACH_SOURCE_CLICKED, "payload");

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
			const costComponent = findComponent(selectedConstructableBuilding, COMPONENT_TYPES.COST);
			if (kingdom.idleBucks < costComponent.idleBucks) {
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
				dispatch(idleBucksChanged(costComponent.idleBucks));
			});

			dispatch(idleBucksChanged(-costComponent.idleBucks));

			return Promise.resolve();
		}

		const engine = gameContainer.engine;
		const entities = engine.getAllEntities();
		const selectedEntity = entities.find(entity => doesContain(entity, { x, y })) || null;

		console.log(selectedEntity);
		dispatch(setSelectedEntity(_.result(selectedEntity, 'getId', null)));

		return Promise.resolve();
	};
};
