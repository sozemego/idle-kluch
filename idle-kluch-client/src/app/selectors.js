import { rootSelector } from "../store/utils";

export const appRoot = rootSelector("app");

export const isFetching = state => appRoot(state).fetchingActions > 0;
export const getErrorMessage = state => appRoot(state).errorMessage || "";
export const getUser = state => appRoot(state).user;
export const isLoggedIn = state => !!getUser(state).token;

export const getUsernameError = state => appRoot(state).usernameError;
export const getPasswordError = state => appRoot(state).passwordError;