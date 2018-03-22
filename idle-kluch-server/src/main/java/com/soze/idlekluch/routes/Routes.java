package com.soze.idlekluch.routes;

/**
 * Interface holding routes for the API.
 */
public interface Routes {

  String BASE = "/api/0.1";

  String USER = "/user";
  String USER_BASE = BASE + USER;
  String USER_GET_ALL = USER_BASE + "/all";
  String USER_REGISTER = USER_BASE + "/register";
  String USER_GET_SINGLE = USER_BASE + "/single";
  String USER_DELETE_SINGLE = USER_BASE + "/single/delete";
  String USER_CHECK_AVAILABLE_USERNAME = USER_BASE + "/single/available";

  String AUTH = "/auth";
  String AUTH_BASE = USER_BASE + AUTH;
  String AUTH_LOGIN = AUTH_BASE + "/login";
  String AUTH_PASSWORD_CHANGE = AUTH_BASE + "/password/change";
  String AUTH_LOGOUT = AUTH_BASE + "/logout";

  String KINGDOM = "/kingdom";
  String KINGDOM_BASE = BASE + KINGDOM;

  String KINGDOM_CREATE = KINGDOM_BASE + "/create";
  String KINGDOM_DELETE = KINGDOM_BASE + "/delete";
  String KINGDOM_GET = KINGDOM_BASE + "/single";
  String KINGDOM_OWN = KINGDOM_BASE + "/own";

  String WEB_SOCKET = "/ws";
  String GAME = "/game";

  String GAME_BASE = BASE + WEB_SOCKET + GAME;


}
