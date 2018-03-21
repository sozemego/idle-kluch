import networkService from '../api/NetworkService';

const basePath = '/kingdom';

const create = `${basePath}/create`;
const deleteKingdom = `${basePath}/delete`;
const getOwn = `${basePath}/own`;
const get = `${basePath}/single`;

const kingdomNameRegexp = new RegExp('^[a-zA-Z0-9_-]+$');
const maxKingdomName = 32;

export const KingdomService = {};