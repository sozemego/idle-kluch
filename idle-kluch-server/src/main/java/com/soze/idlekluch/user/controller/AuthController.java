package com.soze.idlekluch.user.controller;

import com.soze.idlekluch.interceptors.RateLimited;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.user.dto.ChangePasswordForm;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping(path = Routes.AUTH_BASE)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(final AuthService authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @RateLimited(limit = 5, timeUnits = 1)
    @PostMapping(path = Routes.AUTH_LOGIN)
    public ResponseEntity login(@RequestBody final LoginForm loginForm) {
        Jwt token = authService.login(loginForm);
        return ResponseEntity.ok(token);
    }

    @RateLimited(limit = 5, timeUnits = 1)
    @PostMapping(path = Routes.AUTH_PASSWORD_CHANGE)
    public ResponseEntity passwordChange(@RequestBody final ChangePasswordForm changePasswordForm, final Principal principal) {
        authService.passwordChange(principal.getName(), changePasswordForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = Routes.AUTH_LOGOUT)
    public ResponseEntity logout() {
        return ResponseEntity.ok().build();
    }


}
