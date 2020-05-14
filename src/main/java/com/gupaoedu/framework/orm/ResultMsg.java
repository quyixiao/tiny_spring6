package com.gupaoedu.framework.orm;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultMsg <T> implements Serializable {


    private static final long serialVersionUID = 9166821503232103997L;


    private int status; //状态码，系统的返回码

    private String msg ;//  状态码的解释
    private T data ;    //放任意结果

    public ResultMsg() {
    }



    public ResultMsg(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultMsg(int status,  T data) {
        this.status = status;
        this.data = data;
    }

    public ResultMsg(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }





}
