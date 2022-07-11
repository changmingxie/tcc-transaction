package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.serializer.DomainStoreMapSerializer;
import org.mengyun.tcctransaction.serializer.TransactionStoreMapSerializer;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.storage.helper.RedisCommands;
import org.mengyun.tcctransaction.storage.helper.RedisHelper;
import org.mengyun.tcctransaction.storage.helper.ShardHolder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisMovedDataException;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRedisTransactionStorage extends AbstractKVTransactionStorage<Jedis> {


    protected boolean isSupportScan = true;


    public AbstractRedisTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
    }

    @Override
    protected int doCreate(final TransactionStore transactionStore) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid()))) {
            Long statusCode = createByScriptCommand(commands, transactionStore);
            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(final TransactionStore transactionStore) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid()))) {

            Long statusCode = updateByScriptCommand(commands, transactionStore);

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(final TransactionStore transactionStore) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid()))) {

            commands.del(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid()));

            return 1;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doMarkDeleted(TransactionStore transactionStore) {
        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid()))) {
            byte[] oldRedisKey = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            byte[] newRedisKey = RedisHelper.getDeletedRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            Long result = commands.renamenx(oldRedisKey, newRedisKey);
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doRestore(TransactionStore transactionStore) {
        byte[] oldRedisKey = RedisHelper.getDeletedRedisKey(transactionStore.getDomain(), transactionStore.getXid());
        try (RedisCommands commands = getRedisCommands(oldRedisKey)) {
            byte[] newRedisKey = RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid());
            Long result = commands.renamenx(oldRedisKey, newRedisKey);
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected TransactionStore doFindOne(String domain, final Xid xid, boolean isMarkDeleted) {
        return doFind(domain, xid, isMarkDeleted);
    }

    private TransactionStore doFind(String domain, Xid xid, boolean isMarkDeleted) {

        byte[] redisKey = null;
        if (isMarkDeleted) {
            redisKey = RedisHelper.getDeletedRedisKey(domain, xid);
        } else {
            redisKey = RedisHelper.getRedisKey(domain, xid);
        }

        try (RedisCommands commands = getRedisCommands(redisKey)) {

            Long startTime = System.currentTimeMillis();

            Map<byte[], byte[]> content = commands.hgetAll(redisKey);

            if (log.isDebugEnabled()) {
                log.debug("redis find cost time :" + (System.currentTimeMillis() - startTime));
            }

            if (content != null && content.size() > 0) {
                return TransactionStoreMapSerializer.deserialize(content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    protected Long createByScriptCommand(RedisCommands commands, TransactionStore transactionStore) {
        List<byte[]> params = new ArrayList<byte[]>();

        for (Map.Entry<byte[], byte[]> entry : TransactionStoreMapSerializer.serialize(transactionStore)
                .entrySet()) {
            params.add(entry.getKey());
            params.add(entry.getValue());
        }

        Object result = commands.eval(
                "if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 1;"
                        .getBytes(),
                Arrays.asList(RedisHelper.getRedisKey(transactionStore.getDomain(), transactionStore.getXid())),
                params);

        return (Long) result;
    }

    protected Long updateByScriptCommand(RedisCommands commands, TransactionStore transactionStore) {

        Date lastUpdateTime = transactionStore.getLastUpdateTime();
        long currentVersion = transactionStore.getVersion();

        transactionStore.setLastUpdateTime(new Date());
        transactionStore.setVersion(transactionStore.getVersion() + 1);

        try {
            List<byte[]> params = new ArrayList<byte[]>();

            for (Map.Entry<byte[], byte[]> entry : TransactionStoreMapSerializer.serialize(transactionStore)
                    .entrySet()) {
                params.add(entry.getKey());
                params.add(entry.getValue());
            }

            Object result = commands.eval(String.format(
                    "if redis.call('hget',KEYS[1],'VERSION') == '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;",
                    transactionStore.getVersion() - 1).getBytes(),
                    Arrays.asList(RedisHelper.getRedisKey(
                            transactionStore.getDomain(),
                            transactionStore.getXid())),
                    params);
            return (Long) result;
        }finally {
            transactionStore.setLastUpdateTime(lastUpdateTime);
            transactionStore.setVersion(currentVersion);
        }

    }

    @Override
    protected List<TransactionStore> findTransactionsFromOneShard(String domain, Jedis shard, Set keys) {

        List<TransactionStore> list = null;

        Pipeline pipeline = shard.pipelined();

        for (final Object key : keys) {
            pipeline.hgetAll((byte[]) key);
        }

        List<Object> result = pipeline.syncAndReturnAll();

        list = new ArrayList<TransactionStore>();

        for (Object data : result) {

            if (data != null && data instanceof Map && ((Map<byte[], byte[]>) data).size() > 0) {
                list.add(TransactionStoreMapSerializer.deserialize((Map<byte[], byte[]>) data));
            } else if (data instanceof JedisMovedDataException) {
                // ignore the data, this case may happen under redis cluster.
                log.warn("ignore the data, this case may happen under redis cluster.", data);
            } else {
                log.warn("get transactionStore data failed. result is: " + data == null ? "null" : data.toString());
            }
        }

        return list;
    }

    @Override
    Page<byte[]> findKeysFromOneShard(String domain, Jedis shard, String currentCursor, int maxFindCount, boolean isMarkDeleted) {

        Page<byte[]> page = new Page<>();

        String scanPattern = isMarkDeleted ? RedisHelper.DELETED_KEY_PREIFX + "{" + domain + "*" : domain + "*";

        ScanParams scanParams = RedisHelper.buildDefaultScanParams(scanPattern, maxFindCount);

        ScanResult<String> scanResult = shard.scan(currentCursor, scanParams);

        page.setData(scanResult.getResult().stream().map(v -> v.getBytes()).collect(Collectors.toList()));

        page.setAttachment(scanResult.getCursor());

        return page;
    }

    @Override
    public int count(String domain, Jedis shard, boolean isMarkDeleted) {
        String scanPattern = isMarkDeleted ? RedisHelper.DELETED_KEY_PREIFX + "{" + domain + "*" : domain + "*";
        int count = 0;
        String cursor = RedisHelper.REDIS_SCAN_INIT_CURSOR;
        ScanParams scanParams = RedisHelper.scanArgs(scanPattern, RedisHelper.SCAN_MIDDLE_COUNT);
        do {
            ScanResult<String> scanResult = shard.scan(cursor, scanParams);
            count += scanResult.getResult().size();
            cursor = scanResult.getCursor();
        } while (!cursor.equals(RedisHelper.REDIS_SCAN_INIT_CURSOR));
        return count;
    }

    @Override
    public void registerDomain(DomainStore domainStore) {

        byte[] domainStoreRedisKey = RedisHelper.getDomainStoreRedisKey(domainStore.getDomain());
        domainStore.setVersion(1l);
        try (RedisCommands commands = getRedisCommands(domainStoreRedisKey)) {

            List<byte[]> params = new ArrayList<byte[]>();
            for (Map.Entry<byte[], byte[]> entry : DomainStoreMapSerializer.serialize(domainStore)
                    .entrySet()) {
                params.add(entry.getKey());
                params.add(entry.getValue());
            }

            Object result = commands.eval(
                    "if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;"
                            .getBytes(),
                    Arrays.asList(domainStoreRedisKey),
                    params);

            if ((Long) result <= 0) {
                log.info("registerDomain:{} has existed", domainStore.getDomain());
            } else {
                log.info("registerDomain:{} success", domainStore.getDomain());
            }

        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public void updateDomain(DomainStore domainStore) {

        domainStore.setLastUpdateTime(new Date());
        domainStore.setVersion(domainStore.getVersion() + 1);

        byte[] domainStoreRedisKey = RedisHelper.getDomainStoreRedisKey(domainStore.getDomain());

        try (RedisCommands commands = getRedisCommands(domainStoreRedisKey)) {

            List<byte[]> params = new ArrayList<byte[]>();
            for (Map.Entry<byte[], byte[]> entry : DomainStoreMapSerializer.serialize(domainStore).entrySet()) {
                params.add(entry.getKey());
                params.add(entry.getValue());
            }

            Object result = commands.eval(String.format(
                    "if redis.call('hget',KEYS[1],'VERSION') == '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;",
                    domainStore.getVersion() - 1).getBytes(),
                    Arrays.asList(domainStoreRedisKey),
                    params);

            if ((Long) result <= 0) {
                throw new TransactionOptimisticLockException();
            }
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public void removeDomain(String domain) {

        byte[] domainStoreRedisKey = RedisHelper.getDomainStoreRedisKey(domain);

        try (RedisCommands commands = getRedisCommands(domainStoreRedisKey)) {
            commands.del(domainStoreRedisKey);
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public DomainStore findDomain(String domain) {

        byte[] domainStoreRedisKey = RedisHelper.getDomainStoreRedisKey(domain);

        try (RedisCommands commands = getRedisCommands(domainStoreRedisKey)) {

            Map<byte[], byte[]> content = commands.hgetAll(domainStoreRedisKey);

            if (content != null && content.size() > 0) {
                return DomainStoreMapSerializer.deserialize(content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    public List<DomainStore> getAllDomains() {

        String scanPattern = RedisHelper.DOMAIN_KEY_PREIFX + "*";
        String cursor = RedisHelper.REDIS_SCAN_INIT_CURSOR;
        ScanParams scanParams = RedisHelper.scanArgs(scanPattern, 1000);

        List<DomainStore> domainStoreList = new ArrayList<>();
        try (ShardHolder shardHolder = getShardHolder()) {
            List<Jedis> allShards = shardHolder.getAllShards();
            for (Jedis jedis : allShards) {
                List<byte[]> keyList = new ArrayList<>();
                // get all key at current jedis
                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    keyList.addAll(scanResult.getResult().stream().map(v -> v.getBytes()).collect(Collectors.toList()));
                    cursor = scanResult.getCursor();
                } while (!cursor.equals(RedisHelper.REDIS_SCAN_INIT_CURSOR));

                // get all domainStore by keyList
                Pipeline pipeline = jedis.pipelined();
                for (final Object key : keyList) {
                    pipeline.hgetAll((byte[]) key);
                }
                List<Object> result = pipeline.syncAndReturnAll();
                for (Object data : result) {
                    if (data != null && data instanceof Map && ((Map<byte[], byte[]>) data).size() > 0) {
                        domainStoreList.add(DomainStoreMapSerializer.deserialize((Map<byte[], byte[]>) data));
                    } else if (data instanceof JedisMovedDataException) {
                        log.warn("ignore the data, this case may happen under redis cluster.", data);
                    } else {
                        log.warn("get transactionStore data failed. result is: " + data == null ? "null" : data.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }

        return domainStoreList;
    }

    protected abstract RedisCommands getRedisCommands(byte[] shardKey);

    public static class JedisComparator implements Comparator<Jedis> {

        @Override
        public int compare(Jedis jedis1, Jedis jedis2) {
            return String.format("%s:%s:%s", jedis1.getClient().getHost(), jedis1.getClient().getPort(), jedis1.getClient().getDB())
                    .compareTo(String.format("%s:%s:%s",
                            jedis2.getClient().getHost(),
                            jedis2.getClient().getPort(),
                            jedis2.getClient().getDB()));
        }
    }
}
