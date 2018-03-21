import networkService from '../api/NetworkService';

const basePath = '/kingdom';

const create = `${basePath}/create`;
const deleteKingdom = `${basePath}/delete`;
const getOwn = `${basePath}/own`;
const get = `${basePath}/single`;

const kingdomNameRegexp = new RegExp('^[a-zA-Z0-9_-]+$');
const maxKingdomNameLength = 32;

export const KingdomService = {};

KingdomService.getOwn = () => {
  return networkService.get(`${getOwn}`);
};

KingdomService.registerKingdom = (name) => {
  const kingdomNameError = validateKingdomName(name);
  if (kingdomNameError) {
	return Promise.reject({ field: 'name', message: kingdomNameError });
  }

  return networkService.post(`${create}`, {name});
};

const validateKingdomName = (kingdomName) => {
  if (!kingdomName) {
	return 'Kingdom name cannot be empty!';
  }

  if (kingdomName.length > maxKingdomNameLength) {
	return `Kingdom name cannot be longer than ${maxKingdomNameLength}`;
  }

  if (!kingdomNameRegexp.test(kingdomName)) {
	return 'Kingdom name can only contain letters, numbers, - and _';
  }
}