package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.repository.helper.RedisCommands;
import org.mengyun.tcctransaction.repository.helper.RedisHelper;
import org.mengyun.tcctransaction.repository.helper.TransactionStoreSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisMovedDataException;

import javax.transaction.xa.Xid;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRedisTransactionRepository extends AbstractKVStoreTransactionRepository<Jedis> {

    protected boolean isSupportScan = true;

    @Override
    protected int doCreate(final Transaction transaction) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(domain, transaction.getXid()))) {
            Long statusCode = createByScriptCommand(commands, transaction);
            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(final Transaction transaction) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(domain, transaction.getXid()))) {

            Long statusCode = updateByScriptCommand(commands, transaction);

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(final Transaction transaction) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(domain, transaction.getXid()))) {

            Long result = commands.del(RedisHelper.getRedisKey(domain, transaction.getXid()));

            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(final Xid xid) {

        try (RedisCommands commands = getRedisCommands(RedisHelper.getRedisKey(domain, xid))) {

            Long startTime = System.currentTimeMillis();

            Map<byte[], byte[]> content = commands.hgetAll(RedisHelper.getRedisKey(domain, xid));

            if (log.isDebugEnabled()) {
                log.debug("redis find cost time :" + (System.currentTimeMillis() - startTime));
            }

            if (content != null && content.size() > 0) {
                return TransactionStoreSerializer.deserialize(serializer, content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    protected Long createByScriptCommand(RedisCommands commands, Transaction transaction) {
        List<byte[]> params = new ArrayList<byte[]>();

        for (Map.Entry<byte[], byte[]> entry : TransactionStoreSerializer.serialize(serializer, transaction)
                .entrySet()) {
            params.add(entry.getKey());
            params.add(entry.getValue());
        }

        Object result = commands.eval(
                "if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;"
                        .getBytes(),
                Arrays.asList(RedisHelper.getRedisKey(domain, transaction.getXid())),
                params);

        return (Long) result;
    }

    protected Long updateByScriptCommand(RedisCommands commands, Transaction transaction) {

        transaction.setLastUpdateTime(new Date());
        transaction.setVersion(transaction.getVersion() + 1);

        List<byte[]> params = new ArrayList<byte[]>();

        for (Map.Entry<byte[], byte[]> entry : TransactionStoreSerializer.serialize(serializer, transaction)
                .entrySet()) {
            params.add(entry.getKey());
            params.add(entry.getValue());
        }

        Object result = commands.eval(String.format(
                "if redis.call('hget',KEYS[1],'VERSION') == '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;",
                transaction.getVersion() - 1).getBytes(),
                Arrays.asList(RedisHelper.getRedisKey(
                        domain,
                        transaction.getXid())),
                params);

        return (Long) result;
    }

    @Override
    List<Transaction> findTransactionsFromOneShard(Jedis shard, Set<byte[]> keys) {

        List<Transaction> list = null;

        Pipeline pipeline = shard.pipelined();

        for (final byte[] key : keys) {
            pipeline.hgetAll(key);
        }

        List<Object> result = pipeline.syncAndReturnAll();

        list = new ArrayList<Transaction>();

        for (Object data : result) {

            if (data != null && data instanceof Map && ((Map<byte[], byte[]>) data).size() > 0) {
                list.add(TransactionStoreSerializer.deserialize(serializer, (Map<byte[], byte[]>) data));
            } else if (data instanceof JedisMovedDataException) {
                // ignore the data, this case may happen under redis cluster.
                log.warn("ignore the data, this case may happen under redis cluster.", data);
            } else {
                log.warn("get transaction data failed. result is: " + data == null ? "null" : data.toString());
            }
        }

        return list;
    }

    @Override
    Page<byte[]> findKeysFromOneShard(Jedis shard, String currentCursor, int maxFindCount) {

        Page<byte[]> page = new Page<>();

        ScanParams scanParams = RedisHelper.buildDefaultScanParams(domain + "*", maxFindCount);

        ScanResult<String> scanResult = shard.scan(currentCursor, scanParams);

        page.setData(scanResult.getResult().stream().map(v -> v.getBytes()).collect(Collectors.toList()));

        page.setAttachment(scanResult.getCursor());

        return page;
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