package org.mengyun.tcctransaction.storage;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.exception.SystemException;
import org.mengyun.tcctransaction.serializer.TccDomainStoreSerializer;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.storage.helper.RedisHelper;
import org.mengyun.tcctransaction.storage.helper.ShardHolder;
import org.mengyun.tcctransaction.storage.helper.ShardOffset;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RocksDbTransactionStorage extends AbstractKVTransactionStorage<RocksDB> {

    static final Logger log = LoggerFactory.getLogger(RocksDbTransactionStorage.class.getSimpleName());

    static {
        RocksDB.loadLibrary();
    }

    private Options options;

    private RocksDB db;

    private String location;

    private volatile boolean initialized = false;

    private TccDomainStoreSerializer domainStoreSerializer = new TccDomainStoreSerializer();

    public RocksDbTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
        this.location = storeConfig.getLocation();
        init();
    }

    public String getLocation() {
        return location;
    }

    public void init() {

        if (!initialized) {

            synchronized (this) {

                if (!initialized) {
                    if (options == null)
                        // the Options class contains a set of configurable DB options
                        // that determines the behaviour of the database.
                        options = new Options().setCreateIfMissing(true).setKeepLogFileNum(1L);
                    String filePath = this.location;

                    try {
                        db = RocksDB.open(options, filePath);
                    } catch (RocksDBException e) {
                        throw new SystemException("open rocksdb failed.", e);
                    }

                    initialized = true;
                }
            }
        }
    }

    @Override
    protected int doCreate(TransactionStore transactionStore) {

        try {
            byte[] key = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            if (db.get(key) != null) {
                return 0;
            }
            db.put(key, getSerializer().serialize(transactionStore));
            return 1;
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(TransactionStore transactionStore) {

        try {
            TransactionStore foundTransaction = doFindOne(transactionStore.getDomain(), transactionStore.getXid(), false);
            if (foundTransaction == null
                    || foundTransaction.getVersion() != transactionStore.getVersion() - 1
                    || transactionStore.getId() != null && !transactionStore.getId().equals(foundTransaction.getId())) {
                return 0;
            }

            byte[] key = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            db.put(key, getSerializer().serialize(transactionStore));
            return 1;
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(TransactionStore transactionStore) {

        try {
            TransactionStore foundTransaction = doFindOne(transactionStore.getDomain(), transactionStore.getXid(), false);
            if(foundTransaction == null
                    || foundTransaction.getVersion() != transactionStore.getVersion() - 1
                    || transactionStore.getId() != null && !transactionStore.getId().equals(foundTransaction.getId())) {
            }else {
                byte[] key = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
                db.delete(key);
            }
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return 1;
    }

    @Override
    protected int doMarkDeleted(TransactionStore transactionStore) {
        try {
            byte[] key = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            byte[] deleteKey = RedisHelper.getDeletedRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            db.put(deleteKey, getSerializer().serialize(transactionStore));
            db.delete(key);
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return 1;
    }

    @Override
    protected int doRestore(TransactionStore transactionStore) {
        try {
            byte[] key = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            byte[] deleteKey = RedisHelper.getDeletedRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            db.put(key, getSerializer().serialize(transactionStore));
            db.delete(deleteKey);
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return 1;
    }

    @Override
    protected int doCompletelyDelete(TransactionStore transactionStore) {
        try {
            byte[] key = RedisHelper.getDeletedRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            db.delete(key);
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return 1;
    }

    @Override
    protected TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted) {
        byte[] key = RedisHelper.getRedisKey(domain, xid);
        if (isMarkDeleted) {
            key = RedisHelper.getDeletedRedisKey(domain, xid);
        }
        return doFind(db, key);
    }

    @Override
    Page<byte[]> findKeysFromOneShard(String domain, RocksDB shard, String currentCursor, int maxFindCount, boolean isMarkDeleted) {

        Page<byte[]> page = new Page<>();

        try (final RocksIterator iterator = shard.newIterator()) {
            if (ShardOffset.SCAN_INIT_CURSOR.equals(currentCursor)) {
                iterator.seekToFirst();
            } else {
                iterator.seek(currentCursor.getBytes());
            }
            int count = 0;
            while (iterator.isValid() && count < maxFindCount) {
                String key = new String(iterator.key());
                if (!isMarkDeleted && key.startsWith(domain)) {
                    page.getData().add(iterator.key());
                    count++;
                }
                if (isMarkDeleted && key.startsWith(RedisHelper.getDeletedKeyPrefix(domain))) {
                    page.getData().add(iterator.key());
                    count++;
                }
                iterator.next();
            }

            String nextCursor = ShardOffset.SCAN_INIT_CURSOR;
            if (iterator.isValid() && count == maxFindCount) {
                nextCursor = new String(iterator.key());
            }

            page.setAttachment(nextCursor);
        }
        return page;
    }

    @Override
    int count(String domain, RocksDB shard, boolean isMarkDeleted) {
        int count = 0;
        try (final RocksIterator iterator = shard.newIterator()) {
            iterator.seekToFirst();
            while (iterator.isValid()) {
                String key = new String(iterator.key());
                if (!isMarkDeleted && key.startsWith(domain)) {
                    count++;
                }
                if (isMarkDeleted && key.startsWith(RedisHelper.getDeletedKeyPrefix(domain))) {
                    count++;
                }
                iterator.next();
            }
        }
        return count;
    }


    @Override
    protected List<TransactionStore> findTransactionsFromOneShard(String domain, RocksDB shard, Set keys) {

        List<byte[]> allValues = new ArrayList<>();

        try {
            allValues = shard.multiGetAsList(Lists.newLinkedList(keys));
        } catch (RocksDBException e) {
            log.error("get transactionStore data from RocksDb failed.");
        }

        List<TransactionStore> list = new ArrayList<>();

        for (byte[] value : allValues) {

            if (value != null) {
                list.add(getSerializer().deserialize(value));
            }
        }

        return list;
    }


    @Override
    protected ShardHolder<RocksDB> getShardHolder() {
        return new ShardHolder<RocksDB>() {
            @Override
            public List<RocksDB> getAllShards() {
                return Lists.newArrayList(db);
            }

            @Override
            public void close() throws IOException {

            }
        };
    }

    @Override
    public void close() {

        if (db != null) {
            db.close();
        }

        if (options != null) {
            options.close();
        }
    }

    private TransactionStore doFind(RocksDB db, byte[] key) {

        try {
            byte[] values = db.get(key);
            if (ArrayUtils.isNotEmpty(values)) {
                return getSerializer().deserialize(values);
            }
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return null;
    }

    @Override
    public void registerDomain(DomainStore domainStore) {
        try {
            byte[] domainKey = RedisHelper.getDomainStoreRedisKey(domainStore.getDomain());
            db.put(domainKey, domainStoreSerializer.serialize(domainStore));
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public void updateDomain(DomainStore domainStore) {
        domainStore.setVersion(domainStore.getVersion() + 1);
        try {
            byte[] domainKey = RedisHelper.getDomainStoreRedisKey(domainStore.getDomain());
            db.put(domainKey, domainStoreSerializer.serialize(domainStore));
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public void removeDomain(String domain) {
        try {
            byte[] domainKey = RedisHelper.getDomainStoreRedisKey(domain);
            db.delete(domainKey);
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public DomainStore findDomain(String domain) {
        try {
            byte[] domainKey = RedisHelper.getDomainStoreRedisKey(domain);
            return domainStoreSerializer.deserialize(db.get(domainKey));
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public List<DomainStore> getAllDomains() {
        List<DomainStore> list = new ArrayList<>();
        try (final RocksIterator iterator = db.newIterator()) {
            iterator.seekToFirst();
            while (iterator.isValid()) {
                String key = new String(iterator.key());
                if (key.startsWith(RedisHelper.DOMAIN_KEY_PREFIX)) {
                    list.add(domainStoreSerializer.deserialize(iterator.value()));
                }
                iterator.next();
            }
        }
        return list;
    }
}
