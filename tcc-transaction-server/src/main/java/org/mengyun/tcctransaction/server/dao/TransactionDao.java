package org.mengyun.tcctransaction.server.dao;

import org.mengyun.tcctransaction.server.vo.TransactionVo;

import java.util.List;

/**
 * Created by changming.xie on 9/7/16.
 */
public interface TransactionDao {

    public List<TransactionVo> findTransactions(Integer pageNum, int pageSize);

    public Integer countOfFindTransactions();

    public boolean resetRetryCount(String  globalTxId, String branchQualifier);

    public String getDomain();
}

