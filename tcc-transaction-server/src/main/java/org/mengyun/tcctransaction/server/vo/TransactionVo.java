package org.mengyun.tcctransaction.server.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
public class TransactionVo {

    private String domain;

    private String globalTxId;

    private String branchQualifier;

    private Integer status;

    private Integer transactionType;

    private Integer retriedCount;

    private Date createTime;

    private Date lastUpdateTime;

    private String contentView;

    private InvocationInfo aggInvocation;
    private InvocationInfo tccConfimInvocation;
    private InvocationInfo tccCancelInvocation;

    final static private Log logger = LogFactory.getLog(TransactionVo.class);
    public void parser() {
        logger.info("start parse contentView");
        JSONObject obj = JSON.parseObject(contentView);

        // participants 复数
        JSONArray participants = obj.getJSONArray("participants");
        if (participants != null) {
            if (participants.size() == 0) {
                return;
            }
            logger.info("participants is array");
            JSONObject tccParticipants = participants.getJSONObject(0);
            tccConfimInvocation = new InvocationInfo(tccParticipants.getJSONObject("confirmInvocationContext"));
            tccCancelInvocation = new InvocationInfo(tccParticipants.getJSONObject("cancelInvocationContext"));
        }

        // 单数
        JSONObject participant = obj.getJSONObject("participant");
        if (participant != null) {
            logger.info("participants is agg");
            aggInvocation = new InvocationInfo(participant.getJSONObject("invocation"));
        }
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGlobalTxId() {
        return globalTxId;
    }

    public void setGlobalTxId(String globalTxId) {
        this.globalTxId = globalTxId;
    }

    public String getBranchQualifier() {
        return branchQualifier;
    }

    public void setBranchQualifier(String branchQualifier) {
        this.branchQualifier = branchQualifier;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(Integer retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getContentView() {
        return contentView;
    }

    public void setContentView(String contentView) {
        this.contentView = contentView;
    }

    public InvocationInfo getAggInvocation() {
        return aggInvocation;
    }

    public void setAggInvocation(InvocationInfo aggInvocation) {
        this.aggInvocation = aggInvocation;
    }

    public InvocationInfo getTccConfimInvocation() {
        return tccConfimInvocation;
    }

    public void setTccConfimInvocation(InvocationInfo tccConfimInvocation) {
        this.tccConfimInvocation = tccConfimInvocation;
    }

    public InvocationInfo getTccCancelInvocation() {
        return tccCancelInvocation;
    }

    public void setTccCancelInvocation(InvocationInfo tccCancelInvocation) {
        this.tccCancelInvocation = tccCancelInvocation;
    }

}
