package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.helper.ShardHolder;
import org.mengyun.tcctransaction.storage.helper.ShardOffset;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public abstract class AbstractKVTransactionStorage<T> extends AbstractTransactionStorage implements StorageRecoverable {

    static final Logger log = LoggerFactory.getLogger(AbstractKVTransactionStorage.class.getSimpleName());

    public AbstractKVTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
    }

    protected int doFindTotal(String domain, boolean isMarkDeleted) {
        int total = 0;
        try (ShardHolder shardHolder = getShardHolder()) {
            List<T> allShards = shardHolder.getAllShards();
            for (T shard : allShards) {
                int shardTotal = count(domain, shard, isMarkDeleted);
                total = total + shardTotal;
            }
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
        return total;
    }

    protected Page<TransactionStore> doFindAllUnmodifiedSince(String domain, Date date, String offset, int pageSize, boolean isMarkDeleted) {
        List<TransactionStore> fetchedTransactions = new ArrayList<>();
        String tryFetchOffset = offset;
        int haveFetchedCount = 0;
        do {
            Page<TransactionStore> page = doFindAll(domain, tryFetchOffset, pageSize - haveFetchedCount, isMarkDeleted);
            tryFetchOffset = page.getNextOffset();
            for (TransactionStore transactionStore : page.getData()) {
                if (transactionStore.getLastUpdateTime().compareTo(date) < 0) {
                    fetchedTransactions.add(transactionStore);
                }
            }
            haveFetchedCount += page.getData().size();
            if (page.getData().isEmpty() || haveFetchedCount >= pageSize) {
                break;
            }
        } while (true);
        return new Page<>(tryFetchOffset, fetchedTransactions);
    }

    /*
     * offset: shardIndex:cursor,eg = 0:0,1:0
     * */
    protected Page<TransactionStore> doFindAll(String domain, String offset, int maxFindCount, boolean isMarkDeleted) {
        ShardOffset currentShardOffset = new ShardOffset(offset);
        ShardOffset nextShardOffset = new ShardOffset();
        Page<TransactionStore> page = new Page<>();
        try (ShardHolder shardHolder = getShardHolder()) {
            List<T> allShards = shardHolder.getAllShards();
            List<TransactionStore> transactions = findTransactionsFromShards(domain, allShards, currentShardOffset, nextShardOffset, maxFindCount, isMarkDeleted);
            page.setNextOffset(nextShardOffset.toString());
            page.setData(transactions);
            return page;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    private List<TransactionStore> findTransactionsFromShards(String domain, final List<T> allShards, ShardOffset currentShardOffset, ShardOffset nextShardOffset, int maxFindCount, boolean isMarkDeleted) {
        List<TransactionStore> transactions = new ArrayList<>();
        Set<byte[]> allKeySet = new HashSet<>();
        int currentShardIndex = currentShardOffset.getShardIndex();
        String currentCursor = currentShardOffset.getCursor();
        String nextCursor = null;
        while (currentShardIndex < allShards.size()) {
            T currentShard = allShards.get(currentShardIndex);
            Page<byte[]> keyPage = findKeysFromOneShard(domain, currentShard, currentCursor, maxFindCount, isMarkDeleted);
            List<byte[]> keys = keyPage.getData();
            if (!keys.isEmpty()) {
                List<TransactionStore> currentTransactions = findTransactionsFromOneShard(domain, currentShard, new HashSet<>(keys));
                if (CollectionUtils.isEmpty(currentTransactions)) {
                    // ignore, maybe the keys are recovered by other threads!
                    log.info("no transactionStore found while key size:{}", keys.size());
                }
                transactions.addAll(currentTransactions);
            }
            nextCursor = (String) keyPage.getAttachment();
            allKeySet.addAll(keyPage.getData());
            if (CollectionUtils.isEmpty(allKeySet)) {
                if (ShardOffset.SCAN_INIT_CURSOR.equals(nextCursor)) {
                    //end of the jedis, try next jedis again.
                    currentShardIndex += 1;
                    currentCursor = ShardOffset.SCAN_INIT_CURSOR;
                    currentShardOffset.setShardIndex(currentShardIndex);
                    currentShardOffset.setCursor(currentCursor);
                } else {
                    //keys is empty, do while again, start from last cursor position
                    currentCursor = nextCursor;
                }
            } else {
                break;
            }
        }
        if (CollectionUtils.isEmpty(allKeySet)) {
            nextShardOffset.setShardIndex(currentShardIndex);
            nextShardOffset.setCursor(currentCursor);
        } else {
            if (ShardOffset.SCAN_INIT_CURSOR.equals(nextCursor)) {
                nextShardOffset.setShardIndex(currentShardIndex + 1);
                nextShardOffset.setCursor(ShardOffset.SCAN_INIT_CURSOR);
            } else {
                nextShardOffset.setShardIndex(currentShardIndex);
                nextShardOffset.setCursor(nextCursor);
            }
        }
        return transactions;
    }

    @Override
    public Page<TransactionStore> findAllUnmodifiedSince(String domain, Date date, String offset, int pageSize) {
        return doFindAllUnmodifiedSince(domain, date, offset, pageSize, false);
    }

    @Override
    public Page<TransactionStore> findAllDeletedSince(String domain, Date date, String offset, int pageSize) {
        return doFindAllUnmodifiedSince(domain, date, offset, pageSize, true);
    }

    @Override
    public int count(String domain, boolean isMarkDeleted) {
        return doFindTotal(domain, isMarkDeleted);
    }

    @Override
    public boolean supportStorageRecoverable() {
        return true;
    }

    protected abstract List<TransactionStore> findTransactionsFromOneShard(String domain, T shard, Set keys);

    abstract Page findKeysFromOneShard(String domain, T shard, String currentCursor, int maxFindCount, boolean isMarkDeleted);

    abstract int count(String domain, T shard, boolean isMarkDeleted);

    protected abstract ShardHolder<T> getShardHolder();

    public TransactionStoreSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(TransactionStoreSerializer serializer) {
        this.serializer = serializer;
    }
}
