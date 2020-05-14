package com.gupaoedu.framework.orm;


import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> implements BaseDao<T, PK> {


    private String tableName ;


}
