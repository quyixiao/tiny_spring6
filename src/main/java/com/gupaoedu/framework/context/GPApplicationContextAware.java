package com.gupaoedu.framework.context;

import com.gupaoedu.framework.context.support.GPApplicationContext;

/***
 * 通过解耦方式获得Ioc 容器的顶层设计
 * 后面将一个监听器去扫描所有的类，只要实现了这个接口
 * 将自动的调用setApplicationContext()方法，从而将Ioc容器注入到目标类中
 */
public interface GPApplicationContextAware {

    void setApplicationContext(GPApplicationContext applicationContext);
}
