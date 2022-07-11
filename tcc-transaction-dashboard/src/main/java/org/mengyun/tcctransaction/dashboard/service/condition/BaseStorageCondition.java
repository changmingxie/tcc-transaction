package org.mengyun.tcctransaction.dashboard.service.condition;

import org.mengyun.tcctransaction.dashboard.enums.DataFetchType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 15:08
 **/
public abstract class BaseStorageCondition implements Condition {


    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String dataFetchTypeVaule = context.getEnvironment().getProperty("spring.tcc.dashboard.data-fetch-type");
        String storageTypeVaule = context.getEnvironment().getProperty("spring.tcc.storage.storage-type");

        if (StringUtils.isEmpty(dataFetchTypeVaule)) {
            throw new RuntimeException("tcc.dashboard.dataFetchType is empty");
        }

        DataFetchType dataFetchType = DataFetchType.nameOf(dataFetchTypeVaule.toUpperCase());
        if (dataFetchType == null) {
            throw new RuntimeException("tcc.dashboard.dataFetchType:" + dataFetchTypeVaule + " not exist");
        }

        return match(dataFetchTypeVaule, storageTypeVaule);
    }

    abstract boolean match(String dataFetchTypeVaule, String storageTypeVaule);


}
