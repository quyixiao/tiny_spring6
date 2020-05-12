package com.gupaoedu.framework.beans.config;

import java.io.PipedReader;

// 用来保存存储配置文件的信息
// 相当于保存在内存中的配置
public class GPBeanDefinition  {
    private String beanClassName;// 原生Bean的全类名
    private boolean lazyInit = false;// 标记是否延迟加载
    private String factoryBeanName; // 保存beanName，在Ioc容器中存储key


    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
