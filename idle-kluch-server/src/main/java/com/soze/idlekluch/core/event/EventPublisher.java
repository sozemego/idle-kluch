package com.soze.idlekluch.core.event;

/**
 * Interface for event oriented architecture.
 */
public interface EventPublisher {

  void publishEvent(Object event);

}
