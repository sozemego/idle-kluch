package com.soze.idlekluch.user.aop;

import com.soze.idlekluch.user.dto.LoginForm;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthAspect {

  private static final Logger LOG = LoggerFactory.getLogger(AuthAspect.class);

  @Pointcut("execution(public * login(com.soze.idlekluch.user.dto.LoginForm))")
  public void loginCall() {}

  @AfterReturning("loginCall() && args(loginForm)")
  public void logSuccessfulLogin(final JoinPoint joinPoint, final LoginForm loginForm) {
    LOG.info("User [{}] successfully logged in", loginForm.getUsername());
  }

  @AfterThrowing("loginCall() && args(loginForm)")
  public void resetFormAfterLogin(final JoinPoint joinPoint, final LoginForm loginForm) {
    LOG.info("User [{}] unsuccessfully tried to login", loginForm.getUsername());
    loginForm.reset();
  }

}
