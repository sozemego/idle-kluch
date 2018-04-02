package com.soze.idlekluch;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Used for development.
 */
public class BeanDevPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {

    System.out.println("BEAN " + bean);
    System.out.println("BeforeInitialization : " + beanName);

    return bean;
  }

}
