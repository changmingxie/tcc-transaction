package org.mengyun.tcctransaction.alert;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.storage.StorageRecoverable;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.mengyun.tcctransaction.storage.domain.AlertType;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.utils.AlertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

/**
 * @Author huabao.fang
 * @Date 2022/6/15 10:17
 */
public class AlertManager {

    private static final String ALERT_CONTENT_TEMPLATE = "【TCC告警】\n" + "Domain: $domain$\n" + "当前TCC事件堆积数：$errorCount$，超过阈值: $threshold$\n" + "告警间隔时间为$intervalMinutes$分钟，请及时处理！";

    private static Logger logger = LoggerFactory.getLogger(AlertManager.class);

    private AlertManager() {
    }

    /**
     * 异常事件堆积告警
     *
     * @param domain
     * @param currentErrorTransactionCount
     * @param transactionStorage
     */
    public static void tryAlert(String domain, int currentErrorTransactionCount, TransactionStorage transactionStorage) {
        DomainStore domainStore = ((StorageRecoverable) transactionStorage).findDomain(domain);
        if (domainStore == null) {
            logger.warn("domainStore:{} not exist", domain);
            return;
        }
        if (isPermitAlert(currentErrorTransactionCount, domainStore)) {
            boolean success = AlertUtils.dingAlert(domainStore.getDingRobotUrl(), domainStore.getPhoneNumbers(), buildAlertContent(currentErrorTransactionCount, domainStore));
            if (success) {
                domainStore.setLastAlertTime(new Date());
                ((StorageRecoverable) transactionStorage).updateDomain(domainStore);
            } else {
                logger.warn("domain:{} alert failed, currentErrorTransactionCount:{}", domain, currentErrorTransactionCount);
            }
        }
    }

    private static String buildAlertContent(int currentErrorTransactionCount, DomainStore currentDomainStore) {
        return ALERT_CONTENT_TEMPLATE.replace("$domain$", currentDomainStore.getDomain()).replace("$errorCount$", String.valueOf(currentErrorTransactionCount)).replace("$threshold$", String.valueOf(currentDomainStore.getThreshold())).replace("$intervalMinutes$", String.valueOf(currentDomainStore.getIntervalMinutes()));
    }

    private static boolean isPermitAlert(int currentErrorTransactionCount, DomainStore currentDomainStore) {
        return isLegalAlertConfig(currentDomainStore) && isPermitAtAlertTime(currentDomainStore) && isPermitAtThreshold(currentErrorTransactionCount, currentDomainStore);
    }

    /**
     * 检查告警配置是否合法
     *
     * @param currentDomainStore
     * @return
     */
    private static boolean isLegalAlertConfig(DomainStore currentDomainStore) {
        AlertType alertType = currentDomainStore.getAlertType();
        String phoneNumbers = currentDomainStore.getPhoneNumbers();
        int intervalMinutes = currentDomainStore.getIntervalMinutes();
        int threshold = currentDomainStore.getThreshold();
        String dingRobotUrl = currentDomainStore.getDingRobotUrl();
        if (alertType == null || !AlertType.DING.equals(alertType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("alertType is {}, skip alert", alertType);
            }
            return false;
        }
        if (StringUtils.isBlank(phoneNumbers)) {
            if (logger.isDebugEnabled()) {
                logger.debug("phoneNumbers is blank, skip alert");
            }
            return false;
        }
        if (intervalMinutes <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("intervalMinutes is less than zero, skip alert");
            }
            return false;
        }
        if (threshold <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("threshold is less than zero, skip alert");
            }
            return false;
        }
        if (StringUtils.isBlank(dingRobotUrl)) {
            // 当前仅支持钉钉告警
            if (logger.isDebugEnabled()) {
                logger.debug("dingRobotUrl is blank, skip alert");
            }
            return false;
        }
        return true;
    }

    /**
     * 判断告警时间
     *
     * @param currentDomainStore
     * @return
     */
    private static boolean isPermitAtAlertTime(DomainStore currentDomainStore) {
        if (currentDomainStore.getLastAlertTime() == null) {
            return true;
        }
        Date nextAlertTime = DateUtils.addMinutes(currentDomainStore.getLastAlertTime(), currentDomainStore.getIntervalMinutes());
        Date currentTime = new Date();
        return currentTime.getTime() > nextAlertTime.getTime();
    }

    /**
     * 判断告警阈值
     *
     * @param currentErrorTransactionCount
     * @param currentDomainStore
     * @return
     */
    private static boolean isPermitAtThreshold(int currentErrorTransactionCount, DomainStore currentDomainStore) {
        return currentErrorTransactionCount > currentDomainStore.getThreshold();
    }
}
