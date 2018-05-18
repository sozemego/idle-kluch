package com.soze.idlekluch.game.exception;

import com.soze.idlekluch.game.message.MessageRevert;

import java.util.Objects;
import java.util.UUID;

/**
 * A base exception used for errors that happen after player interaction.
 * Messages from players that come through WebSockets have a <code>messageId</code>
 * field which uniquely identifies player message. When something goes wrong
 * a {@link MessageRevert} is sent to the player so the front-end can revert the action.
 */
public class GameException extends RuntimeException {

  private final UUID messageId;

  public GameException(final UUID messageId) {
    this.messageId = Objects.requireNonNull(messageId);
  }

  public UUID getMessageId() {
    return messageId;
  }

}
