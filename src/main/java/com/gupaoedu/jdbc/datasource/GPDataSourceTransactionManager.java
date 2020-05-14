package com.gupaoedu.jdbc.datasource;

import javax.sql.DataSource;

/**
 * Created by Tom on 2019/4/17.
 * 事务，是访问并可能更新数据库中各种数据项的一个程序执行单元
 * 特点，事务是恢复和并发控制的基本单位，事件事物应该具有4个属性，原子生性，一致性，隔离性，持久性，4个属性通常称为 ACID
 *
 * 原子性： 是一个
 */
public class GPDataSourceTransactionManager {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
