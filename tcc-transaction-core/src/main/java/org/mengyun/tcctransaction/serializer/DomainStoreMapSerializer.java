package org.mengyun.tcctransaction.serializer;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.storage.domain.AlertType;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.utils.ByteUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/14 12:50
 **/
public class DomainStoreMapSerializer {

    public static final String DOMAIN = "DOMAIN";
    public static final String MAX_RETRY_COUNT = "MAX_RETRY_COUNT";
    public static final String MAX_RECOVERY_REQUEST_PER_SECOND = "MAX_RECOVERY_REQUEST_PER_SECOND";
    public static final String PHONE_NUMBERS = "PHONE_NUMBERS";
    public static final String ALERT_TYPE = "ALERT_TYPE";
    public static final String THRESHOLD = "THRESHOLD";
    public static final String REACH_LIMIT_THRESHOLD = "REACH_LIMIT_THRESHOLD";
    public static final String INTERVAL_MINUTES = "INTERVAL_MINUTES";
    public static final String LAST_ALERT_TIME = "LAST_ALERT_TIME";
    public static final String DING_ROBOT_URL = "DING_ROBOT_URL";
    public static final String CREATE_TIME = "CREATE_TIME";
    public static final String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
    public static final String VERSION = "VERSION";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private DomainStoreMapSerializer() {
    }

    public static Map<byte[], byte[]> serialize(DomainStore domainStore) {

        Map<byte[], byte[]> map = new HashMap<>();
        putBytesIfValueExist(map, DOMAIN, domainStore.getDomain());
        putBytesIfValueExist(map, MAX_RETRY_COUNT, domainStore.getMaxRetryCount());
        putBytesIfValueExist(map, MAX_RECOVERY_REQUEST_PER_SECOND, domainStore.getMaxRecoveryRequestPerSecond());
        putBytesIfValueExist(map, PHONE_NUMBERS, domainStore.getPhoneNumbers());
        putBytesIfValueExist(map, ALERT_TYPE, domainStore.getAlertType());
        putBytesIfValueExist(map, THRESHOLD, domainStore.getThreshold());
        putBytesIfValueExist(map, REACH_LIMIT_THRESHOLD, domainStore.getReachLimitThreshold());
        putBytesIfValueExist(map, INTERVAL_MINUTES, domainStore.getIntervalMinutes());
        putBytesIfValueExist(map, LAST_ALERT_TIME, domainStore.getLastAlertTime());
        putBytesIfValueExist(map, DING_ROBOT_URL, domainStore.getDingRobotUrl());
        putBytesIfValueExist(map, CREATE_TIME, domainStore.getCreateTime());
        putBytesIfValueExist(map, LAST_UPDATE_TIME, domainStore.getLastUpdateTime());
        putBytesIfValueExist(map, VERSION, domainStore.getVersion());
        return map;
    }

    private static void putBytesIfValueExist(Map<byte[], byte[]> map, String key, Object value) {
        if (value == null) {
            return;
        }
        byte[] valueBytes = null;
        if (value instanceof String) {
            valueBytes = ((String) value).getBytes();
        } else if (value instanceof Integer) {
            valueBytes = ByteUtils.intToBytes((Integer) value);
        } else if (value instanceof Long) {
            valueBytes = ByteUtils.longToBytes((Long) value);
        } else if (value instanceof Date) {
            valueBytes = DateFormatUtils.format((Date) value, DATE_FORMAT).getBytes();
        } else if (value.getClass().isEnum()) {
            valueBytes = value.toString().getBytes();
        } else {
            throw new SystemException("value class:[" + value.getClass().getSimpleName() + "] not support");
        }

        map.put(key.getBytes(), valueBytes);
    }

    public static DomainStore deserialize(Map<byte[], byte[]> map) {

        Map<String, byte[]> propertyMap = new HashMap<>();

        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            propertyMap.put(new String(entry.getKey()), entry.getValue());
        }

        DomainStore domainStore = new DomainStore();
        domainStore.setDomain(new String(propertyMap.get(DOMAIN)));
        domainStore.setMaxRetryCount(propertyMap.get(MAX_RETRY_COUNT) == null ? 3 : ByteUtils.bytesToInt(propertyMap.get(MAX_RETRY_COUNT)));
        domainStore.setMaxRecoveryRequestPerSecond(propertyMap.get(MAX_RECOVERY_REQUEST_PER_SECOND) == null ? 100 : ByteUtils.bytesToInt(propertyMap.get(MAX_RECOVERY_REQUEST_PER_SECOND)));
        domainStore.setPhoneNumbers(propertyMap.get(PHONE_NUMBERS) == null ? null : new String(propertyMap.get(PHONE_NUMBERS)));
        domainStore.setAlertType(propertyMap.get(ALERT_TYPE) == null ? null : AlertType.nameOf(new String(propertyMap.get(ALERT_TYPE))));
        domainStore.setThreshold(propertyMap.get(THRESHOLD) == null ? 0 : ByteUtils.bytesToInt(propertyMap.get(THRESHOLD)));
        domainStore.setReachLimitThreshold(propertyMap.get(REACH_LIMIT_THRESHOLD) == null ? 0 : ByteUtils.bytesToInt(propertyMap.get(REACH_LIMIT_THRESHOLD)));
        domainStore.setIntervalMinutes(propertyMap.get(INTERVAL_MINUTES) == null ? 0 : ByteUtils.bytesToInt(propertyMap.get(INTERVAL_MINUTES)));
        domainStore.setDingRobotUrl(propertyMap.get(DING_ROBOT_URL) == null ? null : new String(propertyMap.get(DING_ROBOT_URL)));
        try {
            domainStore.setLastAlertTime(propertyMap.get(LAST_ALERT_TIME) == null ? null : DateUtils.parseDate(new String(propertyMap.get(LAST_ALERT_TIME)), DATE_FORMAT));
            domainStore.setCreateTime(DateUtils.parseDate(new String(propertyMap.get(CREATE_TIME)), DATE_FORMAT));
            domainStore.setLastUpdateTime(DateUtils.parseDate(new String(propertyMap.get(LAST_UPDATE_TIME)), DATE_FORMAT));
        } catch (ParseException e) {
            throw new SystemException(e);
        }

        domainStore.setVersion(ByteUtils.bytesToLong(propertyMap.get(VERSION)));

        return domainStore;
    }

}
