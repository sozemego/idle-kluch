package com.soze.idlekluch.game.message;

public class PauseStateMessage extends OutgoingMessage {

  private final boolean isRunning;

  public PauseStateMessage(final boolean isRunning) {
    super(OutgoingMessageType.PAUSE_STATE);
    this.isRunning = isRunning;
  }

  public boolean isRunning() {
    return isRunning;
  }
}
