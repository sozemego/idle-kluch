import _ from 'lodash';
import { networkConfig } from '../api/config';

const game = `ws/game`;

let socket = null;

export const GameService = {};

GameService.connect = function() {
  if (socket && socket.readyState !== WebSocket.CLOSED) {
	console.warn('Already connected or connecting or closing. Either way, cannot connect right now.');
	return;
  }

  const { wsProtocol, base, port, version } = networkConfig;
  socket = new WebSocket(`${wsProtocol}://${base}:${port}${version}/${game}`);

  socket.onopen = () => {

  };

  socket.onclose = () => {

  };

  socket.onmessage = (message) => {
    const parsed = JSON.parse(message);
	// const parsed = JSON.parse(message.data);
	// if (parsed['START_TYPING']) {
	//   onStartTyping(parsed['START_TYPING']);
	// }
	// if (parsed['STOP_TYPING']) {
	//   onStopTyping(parsed['STOP_TYPING']);
	// }
	// if (parsed['REPLY']) {
	//   onReply(parsed['REPLY']);
	// }
  };
};

GameService.disconnect = function () {
  if(socket) {
	socket.close();
  }
};

const isOpen = () => {
  return (socket && socket.readyState === WebSocket.OPEN);
};

const isConnecting = () => {
  return (socket && socket.readyState === WebSocket.CONNECTING);
};