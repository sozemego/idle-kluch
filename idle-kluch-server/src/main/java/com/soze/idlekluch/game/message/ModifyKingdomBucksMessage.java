package com.soze.idlekluch.game.message;

public class ModifyKingdomBucksMessage extends OutgoingMessage {

  private final int bucksChange;

  public ModifyKingdomBucksMessage(final int bucksChange) {
    super(OutgoingMessageType.MODIFY_KINGDOM_BUCKS);
    this.bucksChange = bucksChange;
  }

  public int getBucksChange() {
    return bucksChange;
  }
}
