package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.core.utils.ExceptionUtils;
import com.soze.idlekluch.core.utils.http.ErrorResponse;
import com.soze.idlekluch.game.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.game.exception.CannotAffordBuildingException;
import com.soze.idlekluch.game.exception.SpaceAlreadyOccupiedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class KingdomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserAlreadyHasKingdomException.class)
  public ResponseEntity handleUserAlreadyHasKingdomException(final UserAlreadyHasKingdomException e) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "User already has a kingdom");
    errorResponse.addData("kingdom", e.getKingdomName());
    errorResponse.addData("user", e.getUsername());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(UserDoesNotHaveKingdomException.class)
  public ResponseEntity handleUserDoesNotHaveKingdomException(final UserDoesNotHaveKingdomException e) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "User does not have a kingdom");
    errorResponse.addData("user", e.getUsername());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(BuildingDoesNotExistException.class)
  public ResponseEntity handleBuildingDoesNotExistException(BuildingDoesNotExistException exception) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "Building does exist " + exception.getBuildingId());
    errorResponse.addData("field", "buildingId");
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(CannotAffordBuildingException.class)
  public ResponseEntity handleCannotAffordBuildingException(CannotAffordBuildingException exception) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "Cannot afford building");
    errorResponse.addData("buildingId", exception.getBuildingId());
    errorResponse.addData("cost", exception.getCost());
    errorResponse.addData("playerBucks", exception.getPlayerBucks());
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @ExceptionHandler(SpaceAlreadyOccupiedException.class)
  public ResponseEntity handleSpaceAlreadyOccupiedException(SpaceAlreadyOccupiedException exception) {
    final ErrorResponse errorResponse = new ErrorResponse(400, "Space already occupied");
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

}
