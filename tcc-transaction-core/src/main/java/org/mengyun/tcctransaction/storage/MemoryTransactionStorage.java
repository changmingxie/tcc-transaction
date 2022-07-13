package org.mengyun.tcctransaction.storage;

import com.google.common.collect.Lists;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.storage.helper.RedisHelper;
import org.mengyun.tcctransaction.storage.helper.ShardHolder;
import org.mengyun.tcctransaction.storage.helper.ShardOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MemoryTransactionStorage extends AbstractKVTransactionStorage<Map<String, TransactionStore>> {

    static final Logger log = LoggerFactory.getLogger(MemoryTransactionStorage.class.getSimpleName());

    private Map<String, TransactionStore> db = new ConcurrentHashMap<>();

    private Map<String, DomainStore> domainDb = new ConcurrentHashMap<>();

    public MemoryTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
    }

    @Override
    protected List<TransactionStore> findTransactionsFromOneShard(String domain, Map<String, TransactionStore> shard, Set keys) {

        List<TransactionStore> list = new ArrayList<TransactionStore>();

        for (Object key : keys) {
            list.add(shard.get(key));
        }

        return list;
    }

    @Override
    Page<String> findKeysFromOneShard(String domain, Map<String, TransactionStore> shard, String currentCursor, int maxFindCount, boolean isMarkDeleted) {

        Page<String> page = new Page<>();
        Iterator<String> iterator = shard.keySet().iterator();
        int iteratorIndex = 0;
        int currentIndex = Integer.valueOf(currentCursor);

        int count = 0;

        while (iterator.hasNext() && count < maxFindCount) {

            if (iteratorIndex < currentIndex) {
                iteratorIndex++;
                continue;
            }
            String key = iterator.next();
            if (!isMarkDeleted && key.startsWith(domain)) {
                page.getData().add(key);
                count++;
            }
            if (isMarkDeleted && key.startsWith(RedisHelper.getDeletedKeyPreifx(domain))) {
                page.getData().add(key);
                count++;
            }
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
    int count(String domain, Map<String, TransactionStore> shard, boolean isMarkDeleted) {
        int count = 0;
        for(String key: shard.keySet()){
            if (!isMarkDeleted && key.startsWith(domain)) {
                count++;
            }
            if (isMarkDeleted && key.startsWith(RedisHelper.getDeletedKeyPreifx(domain))) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected ShardHolder<Map<String, TransactionStore>> getShardHolder() {
        return new ShardHolder<Map<String, TransactionStore>>() {
            @Override
            public List<Map<String, TransactionStore>> getAllShards() {
                return Lists.newArrayList(db);
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    @Override
    protected int doCreate(TransactionStore transactionStore) {
        trace("create", transactionStore);
        db.put(buildMemoryKey(transactionStore), transactionStore);
        return 1;
    }

    @Override
    protected int doUpdate(TransactionStore transactionStore) {
        trace("update", transactionStore);
        TransactionStore foundTransaction = doFindOne(transactionStore.getDomain(), transactionStore.getXid(), false);
        if (foundTransaction.getVersion() != transactionStore.getVersion()) {
            return 0;
        }

        Date lastUpdateTime = transactionStore.getLastUpdateTime();
        long currentVersion = transactionStore.getVersion();
        transactionStore.setVersion(transactionStore.getVersion() + 1);
        transactionStore.setLastUpdateTime(new Date());
        try {
            //because only the reference is stored here, the parameters are not restored if successful
            db.put(buildMemoryKey(transactionStore), transactionStore);
        } catch (Exception e) {
            transactionStore.setLastUpdateTime(lastUpdateTime);
            transactionStore.setVersion(currentVersion);
        }
        return 1;
    }

    @Override
    protected int doDelete(TransactionStore transactionStore) {
        trace("delete", transactionStore);
        db.remove(buildMemoryKey(transactionStore));
        return 1;
    }

    @Override
    protected int doMarkDeleted(TransactionStore transactionStore) {
        transactionStore.setVersion(transactionStore.getVersion() + 1);
        transactionStore.setLastUpdateTime(new Date());
        db.put(buildMarkDeletedMemoryKey(transactionStore), transactionStore);
        db.remove(buildMemoryKey(transactionStore));
        return 1;
    }

    @Override
    protected int doRestore(TransactionStore transactionStore) {
        transactionStore.setVersion(transactionStore.getVersion() + 1);
        transactionStore.setLastUpdateTime(new Date());
        db.remove(buildMarkDeletedMemoryKey(transactionStore));
        db.put(buildMemoryKey(transactionStore), transactionStore);
        return 1;
    }

    @Override
    protected TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted) {
        if (isMarkDeleted) {
            return db.get(buildMarkDeletedMemoryKey(domain, xid));
        }
        return db.get(buildMemoryKey(domain, xid));
    }

    private void trace(String action, TransactionStore transactionStore) {

        StringBuilder sb = new StringBuilder();

        sb.append("MemoryStoreTransactionRepository." + action + "\r\n");
        sb.append("transactionStore xid:" + transactionStore.getXid() + "; status:" + transactionStore.getStatusId() + "\r\n");
//        sb.append("content:" + new String(transactionStore.getContent()));

        log.debug(sb.toString());
    }

    @Override
    public void registerDomain(DomainStore domainStore) {
        domainStore.setVersion(1);
        domainDb.put(domainStore.getDomain(), domainStore);
    }

    @Override
    public void updateDomain(DomainStore domainStore) {
        domainStore.setVersion(domainStore.getVersion()+1);
        domainDb.put(domainStore.getDomain(), domainStore);
    }

    @Override
    public void removeDomain(String domain) {
        domainDb.remove(domain);
    }

    @Override
    public DomainStore findDomain(String domain) {
        return domainDb.get(domain);
    }

    @Override
    public List<DomainStore> getAllDomains() {
        return domainDb.values().stream().collect(Collectors.toList());
    }

    private String buildMemoryKey(TransactionStore transactionStore) {
        return buildMemoryKey(transactionStore.getDomain(), transactionStore.getXid());
    }

    private String buildMemoryKey(String domain, Xid xid) {
        if (domain.endsWith(RedisHelper.SEPARATOR)) {
            return domain + xid.toString();
        }
        return domain + RedisHelper.SEPARATOR + xid.toString();
    }


    private String buildMarkDeletedMemoryKey(TransactionStore transactionStore) {
        return buildMarkDeletedMemoryKey(transactionStore.getDomain(), transactionStore.getXid());
    }

    private String buildMarkDeletedMemoryKey(String domain, Xid xid) {
        return new String(RedisHelper.getDeletedRedisKey(domain,xid));
    }
}
