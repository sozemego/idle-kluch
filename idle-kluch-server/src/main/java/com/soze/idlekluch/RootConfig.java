package com.soze.idlekluch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(
  value = "com.soze.idlekluch",
  excludeFilters = {@ComponentScan.Filter(org.springframework.stereotype.Controller.class)})
@EnableAspectJAutoProxy
public class RootConfig {

}
