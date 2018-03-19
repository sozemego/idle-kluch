package com.soze.idlekluch;


import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.http.HttpClient;
import org.junit.Before;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used as a base class for tests which requires logged in users.
 */
public class BaseAuthTest {

  private final String registerUserPath = Routes.USER_BASE + Routes.USER_REGISTER;
  private final String login = Routes.AUTH_BASE + Routes.AUTH_LOGIN;

  private final Map<String, String> userTokenMap = new HashMap<>();
  private final Map<String, String> userPasswordMap = new HashMap<>();

  protected HttpClient client;

  @Before
  public void setup() {
    client = new HttpClient("http://localhost:8180");
  }

  protected void login(final String username) {
    register(username);

    userTokenMap.computeIfAbsent(username, (key) -> {
      LoginForm form = new LoginForm(username, userPasswordMap.get(username).toCharArray());
      ResponseEntity response = client.post(form, login);
      Jwt token = JsonUtils.jsonToObject((String) response.getBody(), Jwt.class);
      return token.getJwt();
    });

    client.setToken(userTokenMap.get(username));
  }

  protected void register(final String username) {
    userPasswordMap.computeIfAbsent(username, (key) -> {
      String randomPassword = CommonUtils.generateRandomString(15);
      RegisterUserForm form = new RegisterUserForm(username, randomPassword.toCharArray());

      client.post(form, registerUserPath);
      return randomPassword;
    });
  }

}
