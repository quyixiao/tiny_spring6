package com.gupaoedu.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class GPHandlerMapping  {
    private Object controller;//目标方法所在controller 对象
    private Method method;// URL 对应的目标对象
    private Pattern pattern;// URL 的封装


    public GPHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }


    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
