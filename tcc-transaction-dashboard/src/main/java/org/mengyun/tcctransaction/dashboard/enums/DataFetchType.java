package org.mengyun.tcctransaction.dashboard.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 20:52
 **/
public enum DataFetchType {
    LOCAL,// 本地模式
    TCCSERVER // tccserver模式
    ;

    private static Map<String, DataFetchType> dataFetchTypeMap = new HashMap<>();

    static {
        for (DataFetchType dataFetchType : DataFetchType.values()) {
            dataFetchTypeMap.put(dataFetchType.name(), dataFetchType);
        }
    }

    public static DataFetchType nameOf(String dataFetchTypeName) {
        return dataFetchTypeMap.get(dataFetchTypeName);
    }
}
