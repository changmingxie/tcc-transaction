package org.mengyun.tcctransaction.server.dao;

import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.TransactionVo;

import java.util.List;

/**
 * Created by changming.xie on 9/7/16.
 */
public interface TransactionDao {

    List<TransactionVo> findTransactions(Integer pageNum, int pageSize);

    Integer countOfFindTransactions();
    Integer countOfFindTransactionsDeleted();

    void resetRetryCount(String globalTxId, String branchQualifier);

    void delete(String globalTxId, String branchQualifier);

    void restore(String globalTxId, String branchQualifier);

    void confirm(String globalTxId, String branchQualifier);

    void cancel(String globalTxId, String branchQualifier);

    String getDomain();

    PageDto<TransactionVo> findTransactionPageDto(Integer pageNum, int pageSize);

    PageDto<TransactionVo> findDeleteTransactionPageDto(Integer pageNum, int pageSize);
}

