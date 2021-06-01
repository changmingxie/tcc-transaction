package org.mengyun.tcctransaction.repository.helper;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.SystemException;

public class ShardOffset {

    public static String SCAN_INIT_CURSOR = "0";

    public static String OFFSET_DELIMITER = "::";

    public static String SHARD_OFFSET_FORMAT = "%d" + OFFSET_DELIMITER + "%s";


    private int nodeIndex = 0;
    private String cursor = SCAN_INIT_CURSOR;

    public ShardOffset() {


    }

    public ShardOffset(String offset) {

        if (StringUtils.isNotEmpty(offset)) {

            String[] offsetArray = offset.split(OFFSET_DELIMITER);

            if (offsetArray.length != 2) {
                throw new SystemException("offset invalid. the value is:" + offset);
            }

            this.nodeIndex = Integer.valueOf(offsetArray[0]);
            this.cursor = offsetArray[1];
        }
    }

    public ShardOffset(Integer nodeIndex, String cursor) {
        this.nodeIndex = nodeIndex;
        this.cursor = cursor;
    }

    public int getShardIndex() {
        return nodeIndex;
    }

    public void setShardIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @Override
    public String toString() {
        return String.format(SHARD_OFFSET_FORMAT, nodeIndex,
                StringUtils.isEmpty(cursor) ? SCAN_INIT_CURSOR : cursor);
    }
}
