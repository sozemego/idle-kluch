package com.soze.idlekluch;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[]{
      RootConfig.class, JPAConfiguration.class,
      SecurityConfiguration.class, BeanDevPostProcessor.class,
      SampleContextApplicationListener.class, WebSocketConfiguration.class
    };
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[]{WebConfig.class};
  }

  @Override
  protected String[] getServletMappings() {
    return new String[]{"/"};
  }

  @Override
  protected String getServletName() {
    return "KluchDispatcher";
  }

}
