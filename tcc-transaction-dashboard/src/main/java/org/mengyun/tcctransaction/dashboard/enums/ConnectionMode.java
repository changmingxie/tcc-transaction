package org.mengyun.tcctransaction.dashboard.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 20:52
 * 存储链接模式
 */
public enum ConnectionMode {

    // 嵌入模式
    EMBEDDED,
    //   server模式
    SERVER;

    private static Map<String, ConnectionMode> connectionModeMap = new HashMap<>();

    static {
        for (ConnectionMode connectionMode : ConnectionMode.values()) {
            connectionModeMap.put(connectionMode.name(), connectionMode);
        }
    }

    public static ConnectionMode nameOf(String connectionModeName) {
        return connectionModeMap.get(connectionModeName);
    }
}
