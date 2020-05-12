package com.gupaoedu.framework.beans.config;

public class GPBeanPostProcessor {


    // 为Bean 初始化化之前提供回调接口

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {

        return bean;
    }

    // 在Bean 初始化之后提供调用接口
    public Object postProcessAfterInitializaction(Object bean, String beanName) throws Exception {
        return bean;
    }
}
