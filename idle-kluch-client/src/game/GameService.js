import { networkConfig } from "../api/config";
import Stomp from "stompjs";
import SockJS from "sockjs-client";
import uuid from 'uuid/v4';
import { addEntity, addTiles, removeEntity, setResources, setRunningState, startHarvesting } from "./actions";
import store from "../store/store";
import { getUser } from "../app/selectors";
import { parseJSON } from "../utils/JSONUtils";
import { alreadyConnected } from "../app/actions";
import { default as undoActions } from "./UndoActions";
import { setConstructableBuildings } from "../kingdom/actions";

const getToken = () => getUser(store.getState()).token;

const game = `/game-socket`;

const base = `/game/inbound`;
const buildingBuild = `${base}/build`;
const pauseToggle = `${base}/pause`;
const attachResourceSource = `${base}/attachsource`;

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

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };

const handleMessage = (message) => {
	const parsed = parseJSON(message.body);
	const type = parsed["type"];
	handlerTable[type](parsed);
};

const handlerTable = {
  "WORLD_CHUNK": (message) => store.dispatch(addTiles(message.tiles)),
	"ENTITY": (message) => store.dispatch(addEntity(message)),
	"ALREADY_CONNECTED": (message) => {
		client.disconnect();
		store.dispatch(alreadyConnected(true));
  },
	"MESSAGE_REVERT": (message) => {
		const undoAction = undoActions.getAction(message.messageId);
		undoAction();
  },
	"REMOVE_ENTITY": (message) => store.dispatch(removeEntity(message.entityId)),
	"BUILDING_LIST": (message) => store.dispatch(setConstructableBuildings(message.buildingDefinitions)),
	"PAUSE_STATE": (message) => store.dispatch(setRunningState(message.running)),
	"START_HARVESTING": (message) => store.dispatch(startHarvesting(message.id)),
	"RESOURCE_LIST": (message) => store.dispatch(setResources(message.resources)),
};