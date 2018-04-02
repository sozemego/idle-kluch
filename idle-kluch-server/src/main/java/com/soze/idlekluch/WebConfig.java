package com.soze.idlekluch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(
  basePackageClasses = SecurityWebInitializer.class,
  useDefaultFilters = false,
  includeFilters = {@ComponentScan.Filter(org.springframework.stereotype.Controller.class)})
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS")
      .allowedHeaders("Authorization");
  }

}
