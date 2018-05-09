import { networkConfig } from "../api/config";
import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { addEntity, addTiles } from "./actions";
import store from "../store/store";
import { getUser } from "../app/selectors";
import { parseJSON } from "../utils/JSONUtils";

const getToken = () => getUser(store.getState()).token;

const game = `/game-socket`;

const base = `/game/inbound`;
const buildingBuild = `${base}/build`;

let client = null;

export const GameService = {};

GameService.connect = function () {
  return new Promise((resolve, reject) => {
    // if (client && client.readyState !== WebSocket.CLOSED) {
    //   reject('Already connected or connecting or closing. Either way, cannot connect right now.');
    //   return;
    // }

    const { protocol, base, port, version } = networkConfig;
    const socket = new SockJS(
      `${protocol}://${base}:${port}${version}${game}?token=${getToken()}`,
    );
    client = Stomp.over(socket);

    const messageHandler = message => {
      const parsed = parseJSON(message.body);
      if (parsed[ "type" ] === "WORLD_CHUNK") {
        store.dispatch(addTiles(parsed.tiles));
      }
      if (parsed[ "type" ] === "ENTITY") {
        store.dispatch(addEntity(parsed));
      }
    };

    client.connect({ token: getToken() }, frame => {
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
  client.send(
    buildingBuild,
    {},
    JSON.stringify({ buildingId, x, y, type: "BUILD_BUILDING" }),
  );
};

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };
