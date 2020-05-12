package com.gupaoedu.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.gupaoedu.demo.service.QueryService;
import com.gupaoedu.framework.annotation.GPService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@GPService
public class QuerySerivceImpl implements QueryService {
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        Map<String, String> map = Maps.newHashMap();
        map.put("name", name);
        map.put("time", time);
        return JSON.toJSONString(map);
    }
}
