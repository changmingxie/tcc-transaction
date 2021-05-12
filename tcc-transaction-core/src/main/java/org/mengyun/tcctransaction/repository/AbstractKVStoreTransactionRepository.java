package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.ShardHolder;
import org.mengyun.tcctransaction.repository.helper.ShardOffset;
import org.mengyun.tcctransaction.serializer.KryoTransactionSerializer;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractKVStoreTransactionRepository<T> extends CacheableTransactionRepository {

    static final Logger log = LoggerFactory.getLogger(AbstractKVStoreTransactionRepository.class.getSimpleName());

    String domain;

    TransactionSerializer serializer = new KryoTransactionSerializer();

    public void setSerializer(TransactionSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize) {

        List<Transaction> fetchedTransactions = new ArrayList<>();

        String tryFetchOffset = offset;

        int haveFetchedCount = 0;

        do {

            Page<Transaction> page = doFindAll(tryFetchOffset, pageSize - haveFetchedCount);

            tryFetchOffset = page.getNextOffset();

            for (Transaction transaction : page.getData()) {
                if (transaction.getLastUpdateTime().compareTo(date) < 0) {
                    fetchedTransactions.add(transaction);
                }
            }

            haveFetchedCount += page.getData().size();

            if (page.getData().size() <= 0 || haveFetchedCount >= pageSize) {
                break;
            }
        } while (true);

        return new Page<Transaction>(tryFetchOffset, fetchedTransactions);
    }

    /*
     * offset: shardIndex:cursor,eg = 0:0,1:0
     * */
    protected Page<Transaction> doFindAll(String offset, int maxFindCount) {

        ShardOffset currentShardOffset = new ShardOffset(offset);

        ShardOffset nextShardOffset = new ShardOffset();

        Page<Transaction> page = new Page<Transaction>();

        try (ShardHolder shardHolder = getShardHolder()) {

            List<T> allShards = shardHolder.getAllShards();

            List<Transaction> transactions = findTransactionsFromShards(allShards, currentShardOffset, nextShardOffset, maxFindCount);

            page.setNextOffset(nextShardOffset.toString());
            page.setData(transactions);
            return page;

        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    private List<Transaction> findTransactionsFromShards(final List<T> allShards, ShardOffset currentShardOffset, ShardOffset nextShardOffset, int maxFindCount) {

        List<Transaction> transactions = new ArrayList<>();

        Set<byte[]> allKeySet = new HashSet<>();

        int currentShardIndex = currentShardOffset.getShardIndex();
        String currentCursor = currentShardOffset.getCursor();
        String nextCursor = null;


        while (currentShardIndex < allShards.size()) {

            T currentShard = allShards.get(currentShardIndex);

            Page<byte[]> keyPage = findKeysFromOneShard(currentShard, currentCursor, maxFindCount);
            List<byte[]> keys = keyPage.getData();

            if (keys.size() > 0) {

                List<Transaction> currentTransactions = findTransactionsFromOneShard(currentShard, new HashSet<>(keys));

                if (CollectionUtils.isEmpty(currentTransactions)) {
                    // ignore, maybe the keys are recovered by other threads!
                    log.info("no transaction found while key size:" + keys.size());
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

    abstract List<Transaction> findTransactionsFromOneShard(T shard, Set<byte[]> keys);

    abstract Page<byte[]> findKeysFromOneShard(T shard, String currentCursor, int maxFindCount);

    protected abstract ShardHolder<T> getShardHolder();

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
