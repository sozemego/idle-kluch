import { networkConfig } from "../api/config";
import Stomp from "stompjs";
import SockJS from "sockjs-client";
import uuid from 'uuid/v4';
import {
	addEntity,
	addTiles,
	removeEntity,
	setResources,
	setRunningState,
	startHarvesting,
	startSelling,
	transferResource,
	componentChanged,
} from "./actions";
import store from "../store/store";
import { getUser } from "../app/selectors";
import { parseJSON } from "../utils/JSONUtils";
import { alreadyConnected } from "../app/actions";
import { default as undoActions } from "./UndoActions";
import { idleBucksChanged, setConstructableBuildings, setUpgrades } from "../kingdom/actions";
import { MESSAGE_TYPE } from "./constants";

const getToken = () => getUser(store.getState()).token;

const game = `/game-socket`;

const base = `/game/inbound`;
const buildingBuild = `${base}/build`;
const pauseToggle = `${base}/pause`;
const attachResourceSource = `${base}/attachsource`;
const upgradeComponent = `${base}/upgradecomponent`;

let client = null;

export const GameService = {};

GameService.connect = function () {
  return new Promise((resolve, reject) => {

    const { protocol, base, port, version, app } = networkConfig;
    const socket = new SockJS(
      `${protocol}://${base}:${port}${app}${version}${game}?token=${getToken()}`,
    );
    client = Stomp.over(socket);

    //disable debug messages
    client.debug = () => {};

    client.connect({}, frame => {
      client.subscribe("/user/game/outbound", handleMessage);
      client.subscribe("/game/outbound", handleMessage);
      client.send("/game/inbound/init", {}, null);

      resolve();
    });
  });
};

GameService.disconnect = function () {
  if (client) {
    return new Promise((resolve) => {
      client.disconnect(resolve);
    })
  }
  return Promise.resolve();
};

GameService.constructBuilding = (buildingId, x, y) => {
  const messageId = uuid();
  client.send(
    buildingBuild,
    {},
    JSON.stringify({ messageId, buildingId, x, y, type: "BUILD_BUILDING" }),
  );
  return messageId;
};

GameService.togglePause = () => {
  const messageId = uuid();
  client.send(
    pauseToggle,
    {},
    null,
  );
  return messageId;
};

GameService.attachResourceSource = (harvesterId, sourceId, slot) => {
  const messageId = uuid();
  client.send(
		attachResourceSource,
    {},
		JSON.stringify({ messageId, harvesterId, sourceId, slot, type: "ATTACH_RESOURCE_SOURCE" }),
  );
  return messageId;
};

GameService.upgradeComponent = (entityId, upgradeType) => {
	const messageId = uuid();
	client.send(
		upgradeComponent,
		{},
		JSON.stringify({ messageId, entityId, upgradeType, type: "UPGRADE_COMPONENT" }),
	);
	return messageId;
};

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };

const handleMessage = (message) => {
	const parsed = parseJSON(message.body);
	const type = parsed[ "type" ];
	handlerTable[ type ](parsed);
};

const handlerTable = {
	[ MESSAGE_TYPE.WORLD_CHUNK ]: (message) => store.dispatch(addTiles(message.tiles)),
	[ MESSAGE_TYPE.ENTITY ]: (message) => store.dispatch(addEntity(message)),
	[ MESSAGE_TYPE.ALREADY_CONNECTED ]: (message) => {
		client.disconnect();
		store.dispatch(alreadyConnected(true));
	},
	[ MESSAGE_TYPE.MESSAGE_REVERT ]: (message) => {
		const undoAction = undoActions.getAction(message.messageId);
		undoAction();
	},
	[ MESSAGE_TYPE.REMOVE_ENTITY ]: (message) => store.dispatch(removeEntity(message.entityId)),
	[ MESSAGE_TYPE.BUILDING_LIST ]: (message) => store.dispatch(setConstructableBuildings(message.buildingDefinitions)),
	[ MESSAGE_TYPE.PAUSE_STATE ]: (message) => store.dispatch(setRunningState(message.running)),
	[ MESSAGE_TYPE.START_HARVESTING ]: (message) => store.dispatch(startHarvesting(message.id)),
	[ MESSAGE_TYPE.START_SELLING ]: (message) => store.dispatch(startSelling(message)),
	[ MESSAGE_TYPE.RESOURCE_LIST ]: (message) => store.dispatch(setResources(message.resources)),
	[ MESSAGE_TYPE.TRANSFER_RESOURCE ]: (message) => store.dispatch(transferResource(message)),
	[ MESSAGE_TYPE.MODIFY_KINGDOM_BUCKS ]: (message) => store.dispatch(idleBucksChanged(message.bucksChange)),
	[ MESSAGE_TYPE.COMPONENT_CHANGED]: (message) => store.dispatch(componentChanged(message)),
	[ MESSAGE_TYPE.UPGRADES]: (message) => store.dispatch(setUpgrades(message.upgrades)),
};