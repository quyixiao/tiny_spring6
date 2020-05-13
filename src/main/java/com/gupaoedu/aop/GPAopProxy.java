package com.gupaoedu.aop;


/***
 * 代理工厂顶层接口，提供了获取代理对象的顶层入口
 */
public interface GPAopProxy {
    //
    Object getProxy();


    Object getProxy(ClassLoader classloader);

}
