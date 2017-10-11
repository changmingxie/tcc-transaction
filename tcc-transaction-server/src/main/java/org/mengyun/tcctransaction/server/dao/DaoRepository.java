package org.mengyun.tcctransaction.server.dao;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by changming.xie on 10/11/17.
 */
public class DaoRepository implements ApplicationContextAware {

    Map<String, TransactionDao> domainDaoMap = new HashMap<String, TransactionDao>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TransactionDao> beans = applicationContext.getBeansOfType(TransactionDao.class);

        for (TransactionDao transactionDao : beans.values()) {
            domainDaoMap.put(transactionDao.getDomain(), transactionDao);
        }
    }

    public Collection<String> getDomains() {
        return domainDaoMap.keySet();
    }

    public TransactionDao getDao(String domain) {
        return domainDaoMap.get(domain);
    }
}
