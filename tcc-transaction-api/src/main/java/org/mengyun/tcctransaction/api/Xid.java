package org.mengyun.tcctransaction.api;

public interface Xid {
    int AUTO = 1;
    int CUSTOMIZED = 2;
    int getFormatId();
    String getXid();
}
