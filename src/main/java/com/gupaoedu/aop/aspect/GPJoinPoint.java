package com.gupaoedu.aop.aspect;

import java.lang.reflect.Method;

public interface GPJoinPoint {

    Method getMethod();//业务处理方法

    Object []  getArguments();// 该方法的实参列表

    Object getThis(); // 该方法所属实例对象

    // 在 JoinPoint 中添加自定义属性
    void setUserAttribute(String key,Object value);

    // 从已经添加的自定义属性中获取一个属性值
    Object getUserAttribute(String key);


}
