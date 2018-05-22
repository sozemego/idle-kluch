package com.soze.idlekluch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Used for development.
 */
public class BeanDevPostProcessor implements BeanPostProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(BeanDevPostProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {

//    System.out.println("BEAN " + bean);
//    System.out.println("BeforeInitialization : " + beanName);

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    LOG.info("[{}] initialized", beanName);
    return bean;
  }
}
