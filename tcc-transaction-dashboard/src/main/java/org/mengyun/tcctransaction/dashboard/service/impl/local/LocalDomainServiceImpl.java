package org.mengyun.tcctransaction.dashboard.service.impl.local;

import org.mengyun.tcctransaction.TccClient;
import org.mengyun.tcctransaction.dashboard.service.condition.LocalStorageCondition;
import org.mengyun.tcctransaction.dashboard.service.impl.BaseDomainServiceImpl;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/6/9 17:03
 **/
@Conditional(LocalStorageCondition.class)
@Service
public class LocalDomainServiceImpl extends BaseDomainServiceImpl {

    @Autowired
    private TccClient tccClient;

    @Override
    public TransactionStorage getTransactionStorage() {
        return tccClient.getTransactionStorage();
    }
}
