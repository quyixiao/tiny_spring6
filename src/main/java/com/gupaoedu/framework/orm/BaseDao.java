package com.gupaoedu.framework.orm;


import java.util.List;
import java.util.Map;

public interface BaseDao<T, PK> {
    List<T> select(QueryRule queryRule) throws Exception;


    Page<T> select(QueryRule queryRule, int pageNo, int pageSize) throws Exception;


    List<Map<String, Object>> selectBySql(String sql, Object... args) throws Exception;


    Page<Map<String, Object>> selectBySqlToPage(String sql, Object[] param, int pageNo, int pageSize) throws Exception;

    boolean delete(T entity) throws Exception;

    int deleteAll(List<T> list) throws Exception;


    PK insertAndReturnId(T entity) throws Exception;


    boolean insert(T entity) throws Exception;


    int insertAll(List<T> list) throws Exception;


    boolean update(T entity) throws Exception;


}


