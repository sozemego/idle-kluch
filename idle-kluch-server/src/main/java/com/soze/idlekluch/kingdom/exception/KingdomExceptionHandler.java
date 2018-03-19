package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.utils.ExceptionUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class KingdomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserAlreadyHasKingdomException.class)
  public ResponseEntity handleUserAlreadyHasKingdomException(final UserAlreadyHasKingdomException e) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "User already has a kingdom");
    errorResponse.put("kingdom", e.getKingdomName());
    errorResponse.put("user", e.getUsername());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(UserDoesNotHaveKingdomException.class)
  public ResponseEntity handleUserDoesNotHaveKingdomException(final UserDoesNotHaveKingdomException e) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "User does not have a kingdom");
    errorResponse.put("user", e.getUsername());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }


}
