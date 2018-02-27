package org.mengyun.tcctransaction.server.dto;

import java.util.List;

public class PageDto <T>{

    private List<T> data;

    private Integer pageNum;

    private Integer pageSize;

    private Integer totalCount;

    public PageDto(List<T> data, Integer pageNum, Integer pageSize, Integer totalCount) {
        this.data = data;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }


    public PageDto() {
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
