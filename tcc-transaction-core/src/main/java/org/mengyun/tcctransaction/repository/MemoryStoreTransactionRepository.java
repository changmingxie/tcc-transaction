package org.mengyun.tcctransaction.repository;

import com.google.common.collect.Lists;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import org.mengyun.tcctransaction.repository.helper.ShardOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.Xid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStoreTransactionRepository extends AbstractKVStoreTransactionRepository<Map<Xid, Transaction>> implements LocalStorable {

    static final Logger log = LoggerFactory.getLogger(MemoryStoreTransactionRepository.class.getSimpleName());

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
            }
        };
    }

    @Override
    protected int doCreate(Transaction transaction) {
        trace("create", transaction);
        db.put(transaction.getXid(), transaction);
        return 1;
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        trace("update", transaction);
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
        trace("delete", transaction);
        db.remove(transaction.getXid());
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        return db.get(xid);
    }

    @Override
    protected Transaction doFindRootOne(Xid xid) {
        return db.get(xid);
    }

    private void trace(String action, Transaction transaction) {

        StringBuilder sb = new StringBuilder();

        sb.append("MemoryStoreTransactionRepository." + action + "\r\n");
        sb.append("transaction xid:" + transaction.getXid() + "; status:" + transaction.getStatus() + "\r\n");
        for (Participant participant : transaction.getParticipants()) {
            sb.append("    participant xid:" + participant.getXid() + "\r\n");
            sb.append("        class:" + participant.getConfirmInvocationContext().getTargetClass().getName() +
                    "; confirmMethod:" + participant.getConfirmInvocationContext().getMethodName() +
                    ";cancelMethod:" + participant.getCancelInvocationContext().getMethodName() + "\r\n");
        }

        log.debug(sb.toString());
    }
}
