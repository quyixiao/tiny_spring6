package com.gupaoedu.demo.mvc.action;

import com.gupaoedu.demo.service.ModifyService;
import com.gupaoedu.demo.service.QueryService;
import com.gupaoedu.framework.annotation.GPAutowired;
import com.gupaoedu.framework.annotation.GPController;
import com.gupaoedu.framework.annotation.GPRequestMapping;
import com.gupaoedu.framework.annotation.GPRequestParam;
import com.gupaoedu.framework.webmvc.GPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@GPController
@GPRequestMapping("/web")
public class MyAction {

    @GPAutowired
    private QueryService queryService;

    @GPAutowired
    private ModifyService modifyService;


    @GPRequestMapping("query")
    public GPModelAndView query(HttpServletRequest req, HttpServletResponse resp, @GPRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(resp, result);
    }

    @GPRequestMapping("edit")
    public GPModelAndView edit(HttpServletRequest req, HttpServletResponse resp,
                               @GPRequestParam("id") Long id,
                               @GPRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(resp, result);
    }

    @GPRequestMapping("remove")
    public GPModelAndView remove(HttpServletRequest req, HttpServletResponse resp, @GPRequestParam("id") Long id) {
        String result = modifyService.remove(id);
        return out(resp, result);
    }


    private GPModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
