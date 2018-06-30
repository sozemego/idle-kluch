package com.soze.idlekluch;

import com.soze.idlekluch.core.event.AppStartedEvent;
import com.soze.idlekluch.core.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SampleContextApplicationListener implements ApplicationListener<ApplicationContextEvent> {

  private final EventPublisher eventPublisher;

  private Map<String, ApplicationContext> contextMap = new HashMap<>();

  @Autowired
  public SampleContextApplicationListener(final EventPublisher eventPublisher) {
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
  }

  @Override
  public void onApplicationEvent(ApplicationContextEvent event) {
    if(event.getApplicationContext().getId().equals("org.springframework.web.context.WebApplicationContext:/KluchDispatcher")) {
      eventPublisher.publishEvent(new AppStartedEvent());
    }
    if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
      this.getContextMap().put(event.getApplicationContext().getDisplayName(), event.getApplicationContext());
    }
  }

  public Map<String, ApplicationContext> getContextMap() {
    return contextMap;
  }

  public void log() {
    contextMap.values().forEach(ctx -> System.out.println(Arrays.toString(ctx.getBeanDefinitionNames())));
  }
}