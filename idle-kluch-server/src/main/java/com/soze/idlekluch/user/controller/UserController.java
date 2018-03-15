package com.soze.idlekluch.user.controller;

import com.soze.idlekluch.interceptors.RateLimited;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.dto.SimpleUserDto;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/0.1/user/")
public class UserController {

  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  private UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = Objects.requireNonNull(userService);
  }

  @GetMapping(path = "/all", produces = "application/json")
  public ResponseEntity getAllUsers() {
    List<User> users = userService.getAllUsers();

    List<SimpleUserDto> dtos = users.stream()
      .map(User::getUsername)
      .map(SimpleUserDto::new)
      .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
  }

  @RateLimited(limit = 5, timeUnits = 1)
  @PostMapping(path = "/register", produces = "application/json")
  public ResponseEntity registerUser(@RequestBody final RegisterUserForm registerUserForm) {
    userService.addUser(registerUserForm);
    return ResponseEntity.ok(new SimpleUserDto(registerUserForm.getUsername()));
  }

  @RateLimited
  @GetMapping(path = "/single/{username}", produces = "application/json")
  public ResponseEntity getUserByUsername(@PathVariable("username") final String username) {
    Objects.requireNonNull(username);

    Optional<User> userOptional = userService.getUserByUsername(username);

    if (!userOptional.isPresent()) {
      ErrorResponse errorResponse = new ErrorResponse(404, "User does not exist.");
      errorResponse.addData("username", username);
      return ResponseEntity.status(404).body(errorResponse);
    }

    return ResponseEntity.ok(new SimpleUserDto(userOptional.get().getUsername()));
  }

  @RateLimited
  @DeleteMapping(path = "/single/delete")
  public ResponseEntity deleteUser(final Principal principal) {
    userService.deleteUser(principal.getName());
    return ResponseEntity.ok().build();
  }

  @RateLimited
  @GetMapping(path = "/single/available/{username}")
  public ResponseEntity isUsernameAvailable(@PathVariable("username") final String username) {
    boolean isAvailable = userService.isAvailableForRegistration(username);
    return ResponseEntity.ok(isAvailable);
  }

}
