package com.gupaoedu.framework.orm;

import com.sun.tools.corba.se.idl.constExpr.Times;

import javax.lang.model.type.IntersectionType;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class ClassMappings {

    private ClassMappings() {

    }

    static final Set<Class<?>> SUPPORTED_SQL_OBJECTS = new HashSet<>();

    static {
        Class<?> [] classes = {
                boolean.class,Boolean.class,
                short.class,Short.class,
                int.class, Integer.class,
                long.class,Long.class,
                float.class,Float.class,
                double.class,Double.class,
                String.class,
                Date.class,
                Timestamp.class,
                BigDecimal.class
        };
        SUPPORTED_SQL_OBJECTS .addAll(Arrays.asList(classes));
    }

    static boolean isSupportdSQLObject(Class<?> clazz){
        return clazz.isEnum() || SUPPORTED_SQL_OBJECTS.contains(clazz);
    }

    public static Map<String, Method> findPublicGetters(Class<?> clazz){
        Map<String,Method> map = new HashMap<>();
        Method [] methods  = clazz.getMethods();
        for(Method method: methods){
            if( ){

            }
        }
    }
}
