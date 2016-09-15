package org.mengyun.tcctransaction.server.vo;

import java.util.List;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
public class PageVo<T> {
    private List<T> items;

    private Integer pageNum;

    private Integer pageSize;

    private int pages;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
