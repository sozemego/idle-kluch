export const makeActionCreator = (type, ...argNames) => (...args) => {
  const action = {
    type
  };
  argNames.forEach((item, index) => {
    action[ argNames[ index ] ] = args[ index ];
  });
  return action;
};

export const rootSelector = rootName => state => {
  return typeof state === "function" ? state()[ rootName ] : state[ rootName ];
};

export const createReducer = (initialState, handlers) => {
  return (state = initialState, action) => {
    if (handlers.hasOwnProperty(action.type)) {
      return handlers[ action.type ](state, action);
    } else {
      return state;
    }
  };
};
