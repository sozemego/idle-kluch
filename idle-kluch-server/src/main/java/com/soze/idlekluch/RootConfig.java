package com.soze.idlekluch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(
  value = "com.soze.idlekluch",
  excludeFilters = {@ComponentScan.Filter(org.springframework.stereotype.Controller.class)})
@EnableAspectJAutoProxy
@EnableScheduling
public class RootConfig {

}
