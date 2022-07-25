package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.api.Xid;

import java.io.Closeable;

/**
 * Created by changmingxie on 11/12/15.
 */
public interface TransactionStorage extends Closeable {

    int create(TransactionStore transactionStore);

    int update(TransactionStore transactionStore);

    int delete(TransactionStore transactionStore);

    TransactionStore findByXid(String domain, Xid xid);

    TransactionStore findMarkDeletedByXid(String domain, Xid xid);

    int markDeleted(TransactionStore transactionStore);

    int restore(TransactionStore transactionStore);

    // completely delete for mark deleted transaction
    int completelyDelete(TransactionStore transactionStore);

    boolean supportStorageRecoverable();

    @Override
    default void close() {

    }

}
