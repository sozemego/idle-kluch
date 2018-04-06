// import _ from 'lodash';
import { networkConfig } from '../api/config';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { addBuildings, addTiles } from './actions';
import store from '../store/store';
import { getUser } from '../app/selectors';
import { parseJSON } from '../utils/JSONUtils';

const getToken = () => getUser(store.getState()).token;
const getUsername = () => getUser(store.getState()).name;

const game = `/game-socket`;

const base = `/game/inbound`;
const buildingBuild = `${base}/build`;

let client = null;

export const GameService = {};

GameService.connect = function () {
  return new Promise((resolve, reject) => {
	if (socket && socket.readyState !== WebSocket.CLOSED) {
	  reject('Already connected or connecting or closing. Either way, cannot connect right now.');
	  return;
	}

	const { protocol, base, port, version } = networkConfig;
	const socket = new SockJS(`${protocol}://${base}:${port}${version}${game}?token=${getToken()}`);
	// socket = new WebSocket(`${wsProtocol}://${base}:${port}${version}/${game}`);
	client = Stomp.over(socket);
	client.connect({ token: getToken() }, frame => {

	  client.subscribe('/user/game/outbound', message => {
		const parsed = parseJSON(message.body);
		if (parsed['type'] === 'WORLD_CHUNK') {
		  store.dispatch(addTiles(parsed.tiles));
		}
		if (parsed['type'] === 'CONSTRUCTED_BUILDING') {
		  store.dispatch(addBuildings(parsed.buildings));
		}
	  });
	  client.send('/game/inbound/init', {}, null);

	  resolve();
	});
	// socket = new WebSocket(`${wsProtocol}://${base}:${port}${version}/${game}`);
	//
	// socket.onopen = () => {
	//   resolve();
	// };
	//
	// socket.onclose = () => {
	//
	// };
	//
	// socket.onmessage = (message) => {
	//   const parsed = JSON.parse(message.data);
	//   if (parsed['type'] === 'WORLD_CHUNK') {
	// 	store.dispatch(addTiles(parsed.tiles));
	//   }
	// };
  });
};

GameService.disconnect = function () {
  if (client) {
	client.close();
  }
};

GameService.constructBuilding = (buildingId, x, y) => {
  client.send(buildingBuild, {}, JSON.stringify({buildingId, x, y, type: "BUILD_BUILDING"}));
};

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };