package com.gupaoedu.demo.service.impl;

import com.gupaoedu.demo.service.ModifyService;
import com.gupaoedu.framework.annotation.GPService;


@GPService
public class ModifyServiceImpl implements ModifyService {


    @Override
    public String add(String name, String addr) {
        return "add name :" + name + ",addr:" + addr;
    }

    @Override
    public String edit(Long id, String name) {
        return "edit id :" + id + ",name:" + name;
    }

    @Override
    public String remove(Long id) {
        return "remove id :" + id;
    }
}
