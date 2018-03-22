import {GameService as gameService} from './GameService';

export const connect = () => {
  return (dispatch, getState) => {

    gameService.connect();

  };
};