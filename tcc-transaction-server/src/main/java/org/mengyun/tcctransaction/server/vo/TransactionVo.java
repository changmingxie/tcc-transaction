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

    public static void main(String[] args) {
        JSONObject obj = JSON.parseObject("{\"attachments\":{},\"createTime\":1534238972597,\"lastUpdateTime\":1534238972603,\"participants\":[{\"cancelInvocationContext\":{\"args\":[{\"businessTypeId\":202,\"buyerId\":11264623,\"context\":{\"trackIdsNum\":1,\"compositeItemId\":1010110100013394295,\"isAutoBuy\":true,\"orderTypeId\":1,\"orderFrom\":1,\"title\":\"购买专辑-《阴间神探（VIP版）》紫襟故事\",\"deviceId\":\"5126866d-2de3-342b-9ccc-538500d6f1f3\"},\"domain\":1,\"orderItems\":[{\"itemId\":1010100100086861058,\"quantity\":1,\"skuId\":\"86861058\",\"unitPrice\":0.150000}],\"totalAmount\":0.150000,\"unifiedOrderNo\":\"2018081402020162300145851758\"},{\"businessTypeId\":202,\"buyerDomain\":1,\"buyerId\":11264623,\"payeeDomain\":1,\"payeeId\":1266964,\"payerDomain\":1,\"payerId\":11264623,\"paymentItems\":[{\"amount\":0.150000,\"channelTypeId\":8,\"context\":{\"trackIdsNum\":1,\"orderTypeId\":1,\"title\":\"购买专辑-《阴间神探（VIP版）》紫襟故事\"},\"exchangeRate\":1,\"needRecharge\":false}],\"totalAmount\":0.150000,\"unifiedOrderNo\":\"2018081402020162300145851758\"}],\"methodName\":\"compensableCancelPlaceOrderAndMakeDirectPayment\",\"parameterTypes\":[\"com.ximalaya.business.common.order.command.api.dto.PlaceOrderDto\",\"com.ximalaya.business.common.payment.command.api.dto.PaymentDto\"],\"targetClass\":\"com.ximalaya.business.trade.command.domain.service.OptimizedTradeService\"},\"confirmInvocationContext\":{\"args\":[{\"$ref\":\"$.participants[0].cancelInvocationContext.args.null\"},{\"$ref\":\"$.participants[0].cancelInvocationContext.args[1]\"}],\"methodName\":\"compensableConfirmPlaceOrderAndMakeDirectPayment\",\"parameterTypes\":[\"com.ximalaya.business.common.order.command.api.dto.PlaceOrderDto\",\"com.ximalaya.business.common.payment.command.api.dto.PaymentDto\"],\"targetClass\":\"com.ximalaya.business.trade.command.domain.service.OptimizedTradeService\"},\"terminator\":{},\"xid\":{\"branchQualifier\":\"OWG6mLB6RGW/7lyOSMqzig==\",\"formatId\":1,\"globalTransactionId\":\"BdBP2uXcTe6X5wBxSR/4Rg==\"}}],\"retriedCount\":0,\"status\":\"CONFIRMING\",\"transactionType\":\"ROOT\",\"version\":3,\"xid\":{\"branchQualifier\":\"WwHqjC0VRyOYOWGpjDIjbg==\",\"formatId\":1,\"globalTransactionId\":\"BdBP2uXcTe6X5wBxSR/4Rg==\"}}");
        System.out.println(obj);
    }
}
