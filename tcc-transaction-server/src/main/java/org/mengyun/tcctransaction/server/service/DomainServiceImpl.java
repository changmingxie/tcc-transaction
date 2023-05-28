package org.mengyun.tcctransaction.server.service;

import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.dashboard.service.impl.BaseDomainServiceImpl;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/6/14 12:18
 */
@Service
public class DomainServiceImpl extends BaseDomainServiceImpl {

    @Autowired
    private TccServer tccServer;

    @Override
    public TransactionStorage getTransactionStorage() {
        return tccServer.getTransactionStorage();
    }
}
