package com.gupaoedu.aop.support;

import com.gupaoedu.aop.GPAopConfig;
import com.gupaoedu.aop.aspect.GPAfterReturningAdviceInterceptor;
import com.gupaoedu.aop.aspect.GPAfterThrowingAdviceInterceptor;
import com.gupaoedu.aop.aspect.GPMethodBeforeAdviceInterceptor;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Tom on 2019/4/14.
 */
@Slf4j
public class GPAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private GPAopConfig config;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    public GPAdvisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut();
                /*.replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");*/
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        log.info("aop parse  pointCut="+pointCut);
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("(") - 3);
        log.info("aop parse  pointCutForClassRegex="+pointCutForClassRegex);
        String pointCutClassPatternStr = "class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1) + ".*";
        log.info("aop parse  pointCutClassPatternStr="+pointCutClassPatternStr);
        pointCutClassPattern = Pattern.compile(pointCutClassPatternStr);
        try {
            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);
            log.info(" aop parse pointCutClassPattern :" + pointCutClassPattern + ",pointCutForClassRegex="+pointCutForClassRegex+",pointCut="+pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                log.info("parse aop methodString :" +  methodString);
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //创建一个Advivce
                    Object aspectTarget = aspectClass.newInstance();
                    log.info("parse aop this.targetClass :" +  this.targetClass.getName() + ",method :" + m.getName());
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {

                        advices.add(new GPMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectTarget));
                        log.info("parse aop advices add before :" + aspectTarget.getClass().getName() + ",aspectTarget :" + aspectTarget);
                    }
                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                       // Object aspectTarget = aspectClass.newInstance();
                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectTarget));
                        log.info("parse aop advices add after :" + aspectTarget.getClass().getName() + ",aspectTarget :" + aspectTarget);
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        //Object aspectTarget = aspectClass.newInstance();
                        GPAfterThrowingAdviceInterceptor throwingAdvice =
                        new GPAfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()),
                                aspectTarget);
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                        log.info("parse aop advices add throwing :" + aspectTarget.getClass().getName() + ",aspectTarget :" + aspectTarget);
                    }
                    methodCache.put(m,advices);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
