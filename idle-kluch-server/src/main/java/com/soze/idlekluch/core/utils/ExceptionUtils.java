package com.soze.idlekluch.core.utils;

import com.soze.idlekluch.core.utils.http.ErrorResponse;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class ExceptionUtils {

  /**
   * Converts a {@link ErrorResponse} from the application into a {@link ResponseEntity}.
   *
   * @param errorResponse response to convert
   * @return {@link ResponseEntity}
   */
  public static ResponseEntity convertErrorResponse(final ErrorResponse errorResponse) {
    Objects.requireNonNull(errorResponse);
    return ResponseEntity
               .status(errorResponse.getStatusCode())
               .body(JsonUtils.objectToJson(errorResponse));
  }

}
