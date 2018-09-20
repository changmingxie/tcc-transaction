package org.mengyun.tcctransaction.server.dao;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
        List<String> domains = new ArrayList<String>();
        domains.addAll(domainDaoMap.keySet());
        Collections.sort(domains);
        return domains;
    }

    public TransactionDao getDao(String domain) {
        return domainDaoMap.get(domain);
    }

    public void addDao(TransactionDao dao) {
        domainDaoMap.put(dao.getDomain(), dao);
    }
}
