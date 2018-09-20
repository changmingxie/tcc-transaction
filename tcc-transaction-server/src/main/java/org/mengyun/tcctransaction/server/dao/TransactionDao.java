package org.mengyun.tcctransaction.server.dao;

import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.TransactionVo;

import java.util.List;

/**
 * Created by changming.xie on 9/7/16.
 */
public interface TransactionDao {

    void confirm(String globalTxId, String branchQualifier);

    void cancel(String globalTxId, String branchQualifier);

    void delete(String globalTxId, String branchQualifier);

    void restore(String globalTxId, String branchQualifier);

    void resetRetryCount(String globalTxId, String branchQualifier);

    String getDomain();

    PageDto<TransactionVo> findTransactions(Integer pageNum, int pageSize);

    PageDto<TransactionVo> findDeletedTransactions(Integer pageNum, int pageSize);
}

