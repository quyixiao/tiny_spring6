package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test2 {

    public static void main(String[] args) {
        String pointCut = "class com.gupaoedu.demo.service..*Service.*";

        String methodString = "class com.gupaoedu.demo.service.impl.ModifyServiceImpl";
        Pattern pattern = Pattern.compile(pointCut);



        Matcher matcher = pattern.matcher(methodString);
        System.out.println(matcher.matches());
    }
}
