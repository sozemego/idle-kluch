package com.soze.idlekluch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
  value = "com.soze.idlekluch",
  excludeFilters = {@ComponentScan.Filter(org.springframework.stereotype.Controller.class)})
public class RootConfig {

}
