package com.gupaoedu.framework.context.support;


import com.gupaoedu.framework.beans.config.GPBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPDefaultListableBeanFactory  extends GPAbstractApplicationContext{

    // 存储注册信息BeanDefinition
    protected final Map<String, GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


}
