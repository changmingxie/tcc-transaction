package org.mengyun.tcctransaction.repository;

import com.google.common.collect.Lists;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import org.mengyun.tcctransaction.repository.helper.ShardOffset;

import javax.transaction.xa.Xid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStoreTransactionRepository extends AbstractKVStoreTransactionRepository<Map<Xid, Transaction>> {

    private Map<Xid, Transaction> db = new ConcurrentHashMap<>();

    @Override
    List<Transaction> findTransactionsFromOneShard(Map<Xid, Transaction> shard, Set keys) {

        List<Transaction> list = new ArrayList<Transaction>();

        for (Object key : keys) {
            list.add(shard.get(key));
        }

        return list;
    }

    @Override
    Page<Xid> findKeysFromOneShard(Map<Xid, Transaction> shard, String currentCursor, int maxFindCount) {

        Page<Xid> page = new Page<>();
        Iterator<Xid> iterator = shard.keySet().iterator();
        int iteratorIndex = 0;
        int currentIndex = Integer.valueOf(currentCursor);

        int count = 0;

        while (iterator.hasNext() && count < maxFindCount) {

            if (iteratorIndex < currentIndex) {
                iteratorIndex++;
                continue;
            }

            page.getData().add(iterator.next());
            count++;

            iteratorIndex++;
        }
        String nextCursor = ShardOffset.SCAN_INIT_CURSOR;

        if (iterator.hasNext() && count == maxFindCount) {
            nextCursor = String.valueOf(iteratorIndex);
        }

        page.setAttachment(nextCursor);

        return page;
    }

    @Override
    protected ShardHolder<Map<Xid, Transaction>> getShardHolder() {
        return new ShardHolder<Map<Xid, Transaction>>() {
            @Override
            public List<Map<Xid, Transaction>> getAllShards() {
                return Lists.newArrayList(db);
            }

            @Override
            public void close() throws IOException {
                db.clear();
            }
        };
    }

    @Override
    protected int doCreate(Transaction transaction) {
        db.put(transaction.getXid(), transaction);
        return 1;
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        Transaction foundTransaction = doFindOne(transaction.getXid());
        if (foundTransaction.getVersion() != transaction.getVersion()) {
            return 0;
        }

        transaction.setVersion(transaction.getVersion() + 1);
        transaction.setLastUpdateTime(new Date());
        db.put(transaction.getXid(), transaction);
        return 1;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        db.remove(transaction.getXid());
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        return db.get(xid);
    }
}
