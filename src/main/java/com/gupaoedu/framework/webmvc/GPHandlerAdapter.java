package com.gupaoedu.framework.webmvc;

import com.gupaoedu.framework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// 专人干专事
public class GPHandlerAdapter {


    public boolean supports(Object handler) {

        return (handler instanceof GPHandlerMapping);
    }

    public GPModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        GPHandlerMapping handlerMapping = (GPHandlerMapping) handler;
        // 每一个方法中有一个参数，这里保存的是形参列表
        Map<String, Integer> paramMapping = new HashMap<>();
        // 这里给出的是命名参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof GPRequestParam) {
                    String paramName = ((GPRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }
        // 根据用户用户请求的参数信息，跟Method中的参数信息进行动态匹配
        // resp 传过来的ModelAndView为空的时候，才会新建一个默认的
        // 1.要准备好的这个方法的形参列表
        // 方法重载时形参的决定因素，参数的个数，参数的类型，参数的顺序，方法的名字
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class ||
                    type == HttpServletResponse.class) {
                paramMapping.put(type.getName(), i);
            }
        }
        // 2得到自定义名参数所在的位置
        // 用户通过URL 传过来的参数列表
        Map<String, String[]> reqParameterMap = req.getParameterMap();
        //3.构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramMapping.get(param.getKey());
            //因为页面传过来的值都是String类型的，而在方法中定义的类型是千变万化的，
            // 所以要针对我们的传过来的参数进行类的转换
            paramValues[index] = caseStringValue(value, paramTypes[index]);

        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }
        // 4.从handler 中取出Controller，Method,然后复用反射机制进行调用
        Object result = ((GPHandlerMapping) handler).getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null) {
            return null;
        }
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GPModelAndView.class;
        if (isModelAndView) {
            return (GPModelAndView) result;
        } else {
            return null;
        }

    }


    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }
}
