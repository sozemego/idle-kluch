/**
 * Contains functions that can revert certain actions.
 * When messages are sent to the server, there is no wait to see if it failed or not,
 * the result is applied immediately. If the message fails, a message revert will be sent
 * so that the front-end can revert the action.
 */

const UndoActions = {};

/**
 * MessageId - revert function map.
 */
const actions = {

};

const noop = () => {};

UndoActions.clear = () => {
  Object.keys(actions).forEach(key => {
    delete actions[key];
  })
};

UndoActions.addAction = (messageId, fn) => {
  setTimeout(() => {
    delete actions[messageId];
  }, 10000);
  actions[messageId] = fn;
};

UndoActions.getAction = (messageId) => {
  return actions[messageId] || noop;
}

export default UndoActions;
