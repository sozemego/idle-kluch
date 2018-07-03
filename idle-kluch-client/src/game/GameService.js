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

let client = null;

export const GameService = {};

GameService.connect = function () {
  return new Promise((resolve, reject) => {
    // if (client && client.readyState !== WebSocket.CLOSED) {
    //   reject('Already connected or connecting or closing. Either way, cannot connect right now.');
    //   return;
    // }

    const { protocol, base, port, version, app } = networkConfig;
    const socket = new SockJS(
      `${protocol}://${base}:${port}${app}${version}${game}?token=${getToken()}`,
    );
    client = Stomp.over(socket);

    //disable debug messages
    client.debug = () => {};

    const messageHandler = message => {
      const parsed = parseJSON(message.body);
      const type = parsed["type"];
      if (type === "WORLD_CHUNK") {
        store.dispatch(addTiles(parsed.tiles));
      }

      if (type === "ENTITY") {
        store.dispatch(addEntity(parsed));
      }

      if(type === "ALREADY_CONNECTED") {
        client.disconnect();
        store.dispatch(alreadyConnected(true));
      }

      if(type === "MESSAGE_REVERT") {
        const undoAction = undoActions.getAction(parsed.messageId);
        undoAction();
      }

      if(type === "REMOVE_ENTITY") {
        store.dispatch(removeEntity(parsed.entityId));
      }

      if(type === "BUILDING_LIST") {
        store.dispatch(setConstructableBuildings(parsed.buildingDefinitions));
      }

      if(type === "PAUSE_STATE") {
        store.dispatch(setRunningState(parsed.running));
      }

      if(type === "START_HARVESTING") {
        store.dispatch(startHarvesting(parsed.id))
      }

      if(type === "RESOURCE_LIST") {
        store.dispatch(setResources(parsed.resources))
      }

    };

    client.connect({}, frame => {
      client.subscribe("/user/game/outbound", messageHandler);
      client.subscribe("/game/outbound", messageHandler);
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

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };
