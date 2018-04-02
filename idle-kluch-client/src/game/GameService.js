// import _ from 'lodash';
import { networkConfig } from '../api/config';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { addTiles } from './actions';
import store from '../store/store';
import { getUser } from '../app/selectors';

const getToken = () => getUser(store.getState()).token;
const getUsername = () => getUser(store.getState()).name;

const game = `/game-socket`;

let socket = null;

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
	const client = Stomp.over(socket);
	client.connect({ token: getToken() }, frame => {
	  console.log(frame);

	  client.subscribe('/user/game/outbound', message => {
	    console.log('MESSAGE', message)
	  });
	  client.send('/game/inbound/init', {}, null);

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
  if (socket) {
	socket.close();
  }
};

// const isOpen = () => {
//   return (socket && socket.readyState === WebSocket.OPEN);
// };
//
// const isConnecting = () => {
//   return (socket && socket.readyState === WebSocket.CONNECTING);
// };