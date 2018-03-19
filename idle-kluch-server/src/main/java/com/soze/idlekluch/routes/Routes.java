package com.soze.idlekluch.routes;

/**
 * Interface holding routes for the API.
 */
public interface Routes {

  String BASE = "/api/0.1";

  String USER_BASE = BASE + "/user";
  String USER_GET_ALL = "/all";
  String USER_REGISTER = "/register";
  String USER_GET_SINGLE = "/single/{username}";
  String USER_DELETE_SINGLE = "/single/delete"; //TODO remove delete from path
  String USER_CHECK_AVAILABLE_USERNAME = "/single/available/{username}";

  String AUTH_BASE = BASE + "/auth";
  String AUTH_LOGIN = "/login";
  String AUTH_PASSWORD_CHANGE = "/password/change";
  String AUTH_LOGOUT = "/logout";

  String KINGDOM_BASE = BASE + "/kingdom";


}
