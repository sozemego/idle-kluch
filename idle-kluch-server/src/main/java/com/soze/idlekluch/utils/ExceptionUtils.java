package com.soze.idlekluch.utils;

import com.soze.idlekluch.utils.http.ErrorResponse;
import org.springframework.http.ResponseEntity;

public class ExceptionUtils {

  /**
   * Converts a {@link ErrorResponse} from the application into a {@link ResponseEntity}.
   * @param errorResponse response to convert
   * @return JAX-RS response
   */
  public static ResponseEntity convertErrorResponse(ErrorResponse errorResponse) {
    return ResponseEntity.status(errorResponse.getStatusCode())
      .body(JsonUtils.objectToJson(errorResponse));
  }

}
