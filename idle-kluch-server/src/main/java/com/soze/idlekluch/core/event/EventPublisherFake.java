package com.soze.idlekluch.core.event;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("integration-test")
@Service
public class EventPublisherFake implements EventPublisher {

  @Override
  public void publishEvent(final Object event) {

  }
}
