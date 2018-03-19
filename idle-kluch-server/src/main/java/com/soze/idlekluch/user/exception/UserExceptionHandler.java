package com.soze.idlekluch.user.exception;

import com.soze.idlekluch.utils.ExceptionUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(UserRegistrationException.class)
  public ResponseEntity<Object> handleUserRegistrationException(UserRegistrationException exception) {
    final int statusCode = 400;
    ErrorResponse errorResponse = new ErrorResponse(statusCode, exception.getMessage());
    errorResponse.addData("field", exception.getField());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(AuthUserDoesNotExistException.class)
  public ResponseEntity<Object> handleAuthUserDoesNotExistException(AuthUserDoesNotExistException exception) {
    Map<String, Object> data = new HashMap<>();
    ErrorResponse errorResponse = new ErrorResponse(401, exception.getMessage(), data);
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

}
