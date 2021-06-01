package org.mengyun.tcctransaction.repository;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    private String nextOffset;

    private List<T> data = new ArrayList<>();

    private Object attachment;

    public Page() {

    }

    public Page(String nextOffset, List<T> data) {
        this.nextOffset = nextOffset;
        this.data.addAll(data);
    }

    public String getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(String nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data.clear();

        if (data != null) {
            this.data.addAll(data);
        }
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
}