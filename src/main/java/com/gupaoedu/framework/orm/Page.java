package com.gupaoedu.framework.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {


    private static final long serialVersionUID = 1l;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private long start;     // 当前页的和条数据在 List中的位置
    private List<T> rows;// 当前面中存放的记录，类型一般为 List
    private long total;//   总记录数

    public Page(int pageSize, long start, List<T> rows, long total) {
        this.pageSize = pageSize;
        this.start = start;
        this.rows = rows;
        this.total = total;
    }

    public Page() {
        this(0, 0, new ArrayList<>(), DEFAULT_PAGE_SIZE);
    }


    public int getPageSize() {
        return pageSize;
    }


    public long getTotalPageCount() {
        if (total % pageSize == 0) {
            return total / pageSize;
        } else {
            return total / pageSize + 1;
        }
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public long getPageNo() {
        return start / pageSize + 1;
    }


    public boolean hasNextPage() {
        return this.getPageNo() < this.getTotalPageCount() - 1;
    }


    public boolean hasPreviousPage() {
        return this.getPageNo() > 1;
    }


    protected static int getStartOrPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }


}

