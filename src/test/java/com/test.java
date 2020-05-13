package com;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class test {

    public static void main(String[] args) {
        String pointCut = "class com.gupaoedu.demo.service..*Service.*";
        String methodString = "class com.gupaoedu.demo.service.impl.ModifyServiceImpl";
        //String methodString = "class com.gupaoedu.demo.service.impl.QuerySerivceImpl";
        Pattern pattern = Pattern.compile(pointCut);
        Matcher matcher = pattern.matcher(methodString);
        System.out.println(matcher.matches());

      //  String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("(") - 3);
        //log.info("aop parse  pointCutForClassRegex="+pointCutForClassRegex);
    }
}
