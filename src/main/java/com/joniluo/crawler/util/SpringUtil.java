package com.joniluo.crawler.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by jay on 2017/3/9.
 *
 */
@Component
public final class SpringUtil implements BeanDefinitionRegistryPostProcessor {

  private static ConfigurableListableBeanFactory beanFactory;
  private static BeanDefinitionRegistry registry;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws
      BeansException {
    SpringUtil.beanFactory = beanFactory;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    SpringUtil.registry = registry;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) throws BeansException {
    return (T) beanFactory.getBean(name);
  }

  public static <T> T getBean(Class<T> clz) throws BeansException {
    T result = (T) beanFactory.getBean(clz);
    return result;
  }

  public static boolean containsBean(String name) {
    return beanFactory.containsBean(name);
  }



}
