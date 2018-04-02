package com.soze.idlekluch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import java.util.HashMap;
import java.util.Map;

public class SampleContextApplicationListener implements ApplicationListener<ApplicationContextEvent> {

  private Map<String, ApplicationContext> contextMap = new HashMap<>();

  @Override
  public void onApplicationEvent(ApplicationContextEvent event) {
    if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
      this.getContextMap().put(event.getApplicationContext().getDisplayName(), event.getApplicationContext());
    }
  }

  public Map<String, ApplicationContext> getContextMap() {
    return contextMap;
  }
}