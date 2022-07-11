package org.mengyun.tcctransaction.server.service;

import org.mengyun.tcctransaction.TccServer;
import org.mengyun.tcctransaction.dashboard.service.impl.BaseTransactionServiceImpl;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author huabao.fang
 * @Date 2022/5/24 23:17
 **/
@Service
public class TransactionServiceImpl extends BaseTransactionServiceImpl {

    @Autowired
    private TccServer tccServer;


    @Override
    public TransactionStorage getTransactionStorage() {
        return tccServer.getTransactionStorage();
    }
}
