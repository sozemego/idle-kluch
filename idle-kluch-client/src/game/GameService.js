// import _ from 'lodash';
import { networkConfig } from "../api/config";
import { addTiles } from "./actions";
import store from "../store/store";

const game = `ws/game`;

let socket = null;

export const GameService = {};

GameService.connect = function () {
	return new Promise((resolve, reject) => {
		if (socket && socket.readyState !== WebSocket.CLOSED) {
			reject("Already connected or connecting or closing. Either way, cannot connect right now.");
			return;
		}

		const { wsProtocol, base, port, version } = networkConfig;
		socket = new WebSocket(`${wsProtocol}://${base}:${port}${version}/${game}`);

		socket.onopen = () => {
			resolve();
		};

		socket.onclose = () => {

		};

		socket.onmessage = (message) => {
			const parsed = JSON.parse(message.data);
			if (parsed["type"] === "WORLD_CHUNK") {
				store.dispatch(addTiles(parsed.tiles));
			}
		};
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