package org.mengyun.tcctransaction.repository;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import org.mengyun.tcctransaction.repository.helper.ShardOffset;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.Xid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class RocksDbTransactionRepository extends AbstractKVStoreTransactionRepository<RocksDB> implements LocalStorable {
    static final Logger log = LoggerFactory.getLogger(RocksDbTransactionRepository.class.getSimpleName());

    static {
        RocksDB.loadLibrary();
    }

    private Options options;

    private RocksDB db;

    private String location = "/var/log/";

    private volatile boolean initialized = false;

    public RocksDbTransactionRepository() {

    }

    public void init() throws RocksDBException {

        if (!initialized) {

            synchronized (this) {

                if (!initialized) {
                    if (options == null)
                        // the Options class contains a set of configurable DB options
                        // that determines the behaviour of the database.
                        options = new Options().setCreateIfMissing(true).setKeepLogFileNum(1l);
                    String filePath = getPath(this.location, this.domain);
                    db = RocksDB.open(options, filePath);

                    initialized = true;
                }
            }
        }
    }

    @Override
    protected int doCreate(Transaction transaction) {

        try {
            db.put(transaction.getXid().toString().getBytes(), serializer.serialize(transaction));
            return 1;
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        try {

            Transaction foundTransaction = doFindOne(transaction.getXid());
            if(foundTransaction.getVersion() != transaction.getVersion()) {
                return 0;
            }

            transaction.setVersion(transaction.getVersion() + 1);
            transaction.setLastUpdateTime(new Date());
            db.put(transaction.getXid().toString().getBytes(), serializer.serialize(transaction));
            return 1;
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {

        try {
            db.delete(transaction.getXid().toString().getBytes());
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        try {
            byte[] values = db.get(xid.toString().getBytes());
            if (ArrayUtils.isNotEmpty(values)) {
                return serializer.deserialize(values);
            }
        } catch (RocksDBException e) {
            throw new TransactionIOException(e);
        }
        return null;
    }

    @Override
    Page<byte[]> findKeysFromOneShard(RocksDB shard, String currentCursor, int maxFindCount) {

        Page<byte[]> page = new Page<>();

        try (final RocksIterator iterator = shard.newIterator()) {

            if (ShardOffset.SCAN_INIT_CURSOR.equals(currentCursor)) {
                iterator.seekToFirst();
            } else {
                iterator.seek(currentCursor.getBytes());
            }

            int count = 0;

            while (iterator.isValid() && count < maxFindCount) {

                page.getData().add(iterator.key());

                count++;
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
    List<Transaction> findTransactionsFromOneShard(RocksDB shard, Set keys) {
        List<Transaction> list = null;

        List<byte[]> allValues = null;

        try {
            allValues = shard.multiGetAsList(Lists.newLinkedList(keys));
        } catch (RocksDBException e) {
            log.error("get transaction data from RocksDb failed.");
        }

        list = new ArrayList<Transaction>();

        for (byte[] value : allValues) {

            if (value != null) {
                list.add(serializer.deserialize(value));
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

    public void close() {

        if (db != null) {
            db.close();
        }

        if (options != null) {
            options.close();
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    private String getPath(String location, String domain) {

        StringBuilder stringBuilder = new StringBuilder();

        if (StringUtils.isNotEmpty(location)) {
            stringBuilder.append(location);
            if (!location.endsWith("/")) {
                stringBuilder.append("/");
            }
        }

        stringBuilder.append(domain);

        return stringBuilder.toString();
    }
}
