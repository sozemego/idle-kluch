package com.soze.idlekluch.core.event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventPublisherImpl implements EventPublisher {

  private final EventPublisher applicationEventPublisher;

  @Autowired
  public EventPublisherImpl(final EventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher);
  }

  @Override
  public void publishEvent(final Object event) {
    Objects.requireNonNull(event);
    applicationEventPublisher.publishEvent(event);
  }
}
