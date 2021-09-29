package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;

import javax.transaction.xa.Xid;
import java.io.Closeable;
import java.util.Date;

/**
 * Created by changmingxie on 11/12/15.
 */
public interface TransactionRepository extends Closeable {

    String getDomain();

    String getRootDomain();

    int create(Transaction transaction);

    int update(Transaction transaction);

    int delete(Transaction transaction);

    Transaction findByXid(Xid xid);

    Transaction findByRootXid(Xid xid);

    Page<Transaction> findAllUnmodifiedSince(Date date, String offset, int pageSize);

    @Override
    default void close() {

    }
}
