package com.gupaoedu.aop;

import com.alibaba.fastjson.support.spring.annotation.FastJsonFilter;
import lombok.Data;

@Data
public class GPAopConfig {
    // 以下配制和 properties 文件中的属性是一一对应的
    private String pointCut;//切面表达式
    private String aspectBefore;//前置通知方法名
    private String aspectAfter;//后置通知方法名
    private String aspectClass;//要织入的切面类
    private String aspectAfterThrow;//异常通知方法
    private String aspectAfterThrowingName; // 需要通知的异常类型







}
