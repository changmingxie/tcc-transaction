package org.mengyun.tcctransaction.server.vo;

import lombok.Data;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
@Data
public class TransactionVo {

    private String domain;
    private String status;
    private String transactionType;
    private String retriedCount;
    private String createTime;
    private String lastUpdateTime;
    private String contentView;
    private String globalTxId;
    private String branchQualifier;


}
