package com.gupaoedu.framework.core;

/***
 * 单例工厂顶层设计
 */
public interface GPBeanFactory {
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;

}
