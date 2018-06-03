package com.soze.idlekluch.core.event;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventPublisherImpl implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public EventPublisherImpl(final ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher);
  }

  @Override
  public void publishEvent(final Object event) {
    Objects.requireNonNull(event);
    applicationEventPublisher.publishEvent(event);
  }
}
