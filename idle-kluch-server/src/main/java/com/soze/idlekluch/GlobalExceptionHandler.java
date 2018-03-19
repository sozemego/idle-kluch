package com.soze.idlekluch;


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
  public ResponseEntity<Object> handleRateLimitException(RateLimitException exception) {
    Map<String, Object> data = new HashMap<>();
    data.put("resource", exception.getLimitedResource());
    data.put("limit", exception.getRateLimit());
    data.put("next", exception.getNextRequest());
    ErrorResponse errorResponse = new ErrorResponse(429, "Rate limit exceeded", data);

    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

}
