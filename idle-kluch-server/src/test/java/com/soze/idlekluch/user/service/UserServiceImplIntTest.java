package com.soze.idlekluch.user.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.UserRegistrationException;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class UserServiceImplIntTest {

  @Autowired
  private UserService userService;

  @BeforeClass
  public static void setup() {
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testCreateUser() {
    final String username = "sozemego";
    userService.addUser(new RegisterUserForm(username, "password".toCharArray()));

    final User user = userService.getUserByUsername(username).get();

    assertTrue(user.getUsername().equals(username));
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUserAlreadyExists() {
    final String username = "sozemego1";
    userService.addUser(new RegisterUserForm(username, "password".toCharArray()));
    userService.addUser(new RegisterUserForm(username, "password".toCharArray()));
  }

  @Test
  public void testGetUserDoesNotExist() {
    assertFalse(userService.getUserByUsername("SOME_USER").isPresent());
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUsernameWithWhiteSpaceInside() {
    userService.addUser(new RegisterUserForm("some whitespace", "password".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUserWithWhiteSpaceAtTheEnd() {
    userService.addUser(new RegisterUserForm("some_whitespace ", "password".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUserWithWhiteSpaceAtTheBeginning() {
    userService.addUser(new RegisterUserForm(" some_whitespace", "password".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUserWithWhiteSpaceOnly() {
    userService.addUser(new RegisterUserForm("           ", "password".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void testCreateUserWithIllegalCharacters() {
    userService.addUser(new RegisterUserForm("[]@#$", "password".toCharArray()));
  }

  @Test
  public void testCreateUserWithAllAllowableCharacters() {
    userService.addUser(new RegisterUserForm("qwertyuiopasdfghjklzxcvbnm1234567890-_", "password".toCharArray()));
  }

  @Test
  public void testGetUserByUsernameCaseDoesNotMatter() {
    userService.addUser(new RegisterUserForm("cool_super_name_fine", "password".toCharArray()));

    assertTrue(userService.getUserByUsername("cool_super_name_fine").isPresent());
    assertTrue(userService.getUserByUsername("cool_super_NAME_fine").isPresent());
    assertTrue(userService.getUserByUsername("cool_SUPER_name_fine").isPresent());
    assertTrue(userService.getUserByUsername("COOL_super_name_fine").isPresent());
    assertTrue(userService.getUserByUsername("CoOl_SuPeR_NaMe_fine").isPresent());
  }

  @Test(expected = UserRegistrationException.class)
  public void testRegisterUsernameAlreadyExistsDifferentCase() {
    userService.addUser(new RegisterUserForm("cool_super_name_another", "password".toCharArray()));
    userService.addUser(new RegisterUserForm("cooL_Super_Name_another", "password".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void testRegisterUserNameTooLong() {
    userService.addUser(new RegisterUserForm(CommonUtils.generateRandomString(500), "password".toCharArray()));
  }

  @Test
  public void testUsernameAvailable() {
    assertTrue(userService.isAvailableForRegistration("YES"));
  }

  @Test
  public void testUsernameNotAvailable() {
    final String username = "already_taken";
    userService.addUser(new RegisterUserForm(username, "password".toCharArray()));
    assertFalse(userService.isAvailableForRegistration(username));
  }

  @Test
  public void testInvalidUsernameAvailable() {
    assertFalse(userService.isAvailableForRegistration(CommonUtils.generateRandomString(500)));
  }

  @Test(expected = UserRegistrationException.class)
  public void createUserEmptyPassword() {
    userService.addUser(new RegisterUserForm(CommonUtils.generateRandomString(12), "".toCharArray()));
  }

  @Test(expected = UserRegistrationException.class)
  public void createUserPasswordTooLong() {
    userService.addUser(new RegisterUserForm(
      CommonUtils.generateRandomString(12),
      CommonUtils.generateRandomString(500).toCharArray())
    );
  }







}