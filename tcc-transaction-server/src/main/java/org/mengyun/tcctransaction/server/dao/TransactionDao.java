package org.mengyun.tcctransaction.server.dao;

import org.mengyun.tcctransaction.server.vo.TransactionVo;

import java.util.List;

/**
 * Created by changming.xie on 9/7/16.
 */
public interface TransactionDao {

    public List<TransactionVo> findTransactions(String domain, Integer pageNum, int pageSize);

    public Integer countOfFindTransactions(String domain);

    public boolean resetRetryCount(String domain, byte[] globalTxId, byte[] branchQualifier);
}

