import { rootSelector } from '../store/utils';

export const root = rootSelector('kingdom');

export const getKingdom = state => root(state).kingdom;
export const hasKingdom = state => !!getKingdom(state);
export const showCreateKingdomForm = state => root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state => root(state).kingdomNameRegistrationError;

