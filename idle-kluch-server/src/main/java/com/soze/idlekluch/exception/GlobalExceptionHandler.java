package com.soze.idlekluch.exception;


import com.soze.idlekluch.interceptors.RateLimitException;
import com.soze.idlekluch.utils.ExceptionUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<Object> handleRateLimitException(final RateLimitException exception) {
    final Map<String, Object> data = new HashMap<>();
    data.put("resource", exception.getLimitedResource());
    data.put("limit", exception.getRateLimit());
    data.put("next", exception.getNextRequest());
    final ErrorResponse errorResponse = new ErrorResponse(429, "Rate limit exceeded", data);

    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<Object> handleEntityAlreadyExistsException(final EntityAlreadyExistsException exception) {
    final Map<String, Object> data = new HashMap<>();
    data.put("entity", exception.getClazz().getSimpleName());

    final ErrorResponse errorResponse = new ErrorResponse(400, "Entity already exists", data);
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(EntityDoesNotExistException.class)
  public ResponseEntity<Object> handleEntityDoesNotExistException(final EntityDoesNotExistException exception) {
    final Map<String, Object> data = new HashMap<>();
    data.put("entity", exception.getClazz().getSimpleName());

    final ErrorResponse errorResponse = new ErrorResponse(400, "Entity does not exist", data);
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(InvalidFormException.class)
  public ResponseEntity<Object> handleInvalidFormException(final InvalidFormException exception) {
    final Map<String, Object> data = new HashMap<>();
    data.put("property", exception.getPropertyPath());

    final ErrorResponse errorResponse = new ErrorResponse(400, exception.getMessage(), data);
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<Object> handleException(final Exception exception) {
//    final Map<String, Object> data = new HashMap<>();
//
//    final ErrorResponse errorResponse = new ErrorResponse(400, "Problem while processing this request", data);
//    return ExceptionUtils.convertErrorResponse(errorResponse);
//  }


}
