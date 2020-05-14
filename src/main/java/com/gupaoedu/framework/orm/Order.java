package com.gupaoedu.framework.orm;

public class Order {

    private boolean ascending ; // 升序还是降序

    private String propertyName;// 哪个字段升序，哪个字段降序

    public String toString(){
        return propertyName + " " + (ascending  ? " asc " : " desc ");
    }

    protected Order(String propertyName ,boolean ascending){

    }
}
