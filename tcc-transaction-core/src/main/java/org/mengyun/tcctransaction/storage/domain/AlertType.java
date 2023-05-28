package org.mengyun.tcctransaction.storage.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/13 22:54
 */
public enum AlertType {

    // 钉钉告警
    DING,
    // 短信告警
    SMS,
    // 电话告警
    PHONE;

    private static Map<String, AlertType> alertTypeMap = new HashMap<>(3);

    static {
        for (AlertType alertType : AlertType.values()) {
            alertTypeMap.put(alertType.name(), alertType);
        }
    }

    public static AlertType nameOf(String name) {
        return alertTypeMap.get(name);
    }
}
