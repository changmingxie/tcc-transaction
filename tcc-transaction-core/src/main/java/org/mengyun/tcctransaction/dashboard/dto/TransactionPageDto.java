package org.mengyun.tcctransaction.dashboard.dto;

import java.util.List;

/**
 * @Author huabao.fang
 * @Date 2022/5/23 07:13
 **/
public class TransactionPageDto {
    private String nextOffset;
    private List<TransactionStoreDto> items;

    private int total;

    public String getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(String nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<TransactionStoreDto> getItems() {
        return items;
    }

    public void setItems(List<TransactionStoreDto> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
