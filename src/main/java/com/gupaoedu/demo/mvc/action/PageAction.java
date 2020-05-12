package com.gupaoedu.demo.mvc.action;

import com.gupaoedu.demo.service.QueryService;
import com.gupaoedu.framework.annotation.GPAutowired;
import com.gupaoedu.framework.annotation.GPController;
import com.gupaoedu.framework.annotation.GPRequestMapping;
import com.gupaoedu.framework.annotation.GPRequestParam;
import com.gupaoedu.framework.webmvc.GPModelAndView;

import java.util.HashMap;
import java.util.Map;

@GPController
@GPRequestMapping("/")
public class PageAction {


    @GPAutowired
    private QueryService queryService;


    @GPRequestMapping("/first.html")
    public GPModelAndView query(@GPRequestParam("teacher") String teacher) {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GPModelAndView("first.html", model);
    }

}
