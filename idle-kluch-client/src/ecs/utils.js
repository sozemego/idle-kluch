import _ from 'lodash';

/**
 * Tries to find a component in an entity json.
 * This is not for use with Entity class, but a json representation send from backend.
 */
export const findComponent = (entity, componentType) => {
  const components = _.get(entity, 'components', []);

  return _.find(components, { componentType });
};