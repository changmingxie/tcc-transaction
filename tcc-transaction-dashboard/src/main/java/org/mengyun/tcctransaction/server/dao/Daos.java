package org.mengyun.tcctransaction.server.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisClusterRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisNode;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisShardedRegistration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Lee on 2020/9/12 14:08.
 * aggregate-framework
 */
@Component
@Slf4j
public class Daos {


    private static final String KEY_FORMAT = "redis://%s:%s/%s";
    private final Cache<String, Map<String, TransactionDao>> cache = Caffeine.newBuilder().build();

    @EventListener(classes = JedisShardedRegistration.class)
    public synchronized void onModified(JedisShardedRegistration registration) {

        String domain = registration.getDomain();

        Map<String, TransactionDao> d = new HashMap<>();

        registration.getNodes().forEach(node -> {


            String host = node.getHost();
            String password = node.getPassword();
            int port = node.getPort();
            int database = node.getDatabase();
            String k = String.format(KEY_FORMAT, host, port, database);
            try {
                d.put(k, new RedisTransactionDao(domain, host, port, database, password));
            } catch (Exception e) {
                log.error("连接异常", e);
            }
        });

        cache.put(domain, d);

    }

    @EventListener(classes = JedisClusterRegistration.class)
    public synchronized void onModified1(JedisClusterRegistration registration) {

//        String domain = registration.getDomain();
//
//        Map<String, TransactionDao> d = new HashMap<>();
//
//        registration.getNodes().forEach(node -> {
//
//
//            String host = node.getHost();
//            String password = node.getPassword();
//            int port = node.getPort();
//            int database = node.getDatabase();
//            String k = String.format(KEY_FORMAT, host, port, database);
//            d.put(k, new RedisTransactionDao(domain, host, port, database, password));
//        });
//
//        cache.put(domain, d);


        log.info("JedisClusterRegistration {}", registration);

        String domain = registration.getDomain();

        AtomicReference<String> password = new AtomicReference<>(null);

        Set<HostAndPort> nodes = registration.getNodes()
                .stream()
                .map(new Function<JedisNode, HostAndPort>() {
                    @Override
                    public HostAndPort apply(JedisNode js) {
                        password.set(js.getPassword());
                        return new HostAndPort(js.getHost(), js.getPort());
                    }
                })
                .collect(Collectors.toSet());

        Supplier<JedisCluster> cluster = () -> new JedisCluster(nodes, 2000, 2000, 3, password.get(), new JedisPoolConfig());

        Map<String, TransactionDao> d = new HashMap<>();
        registration.getNodes().forEach(node -> {


            String host = node.getHost();
            int port = node.getPort();
            int database = node.getDatabase();
            String k = String.format(KEY_FORMAT, host, port, database);

            Supplier<JedisPool> jedis = () -> new JedisPool(new JedisPoolConfig(), host, port, 2000, node.getPassword(), database, false);
            try {
                RedisClusterTransactionDao dao = new RedisClusterTransactionDao(cluster, jedis, registration.getDomain());
                d.put(k, dao);
            } catch (Exception e) {
                log.error("连接异常", e);
            }
        });

        cache.put(domain, d);

    }


    @EventListener(classes = JedisRegistration.class)
    public synchronized void onModified01(JedisRegistration registration) {


        String domain = registration.getDomain();

        Map<String, TransactionDao> d = new HashMap<>();

        JedisNode node = registration.getNode();

        String host = node.getHost();
        String password = node.getPassword();
        int port = node.getPort();
        int database = node.getDatabase();
        String k = String.format(KEY_FORMAT, host, port, database);

        try {
            d.put(k, new RedisTransactionDao(domain, host, port, database, password));
        } catch (Exception e) {
            log.error("连接异常", e);
        }

        cache.put(domain, d);

    }

    @EventListener(classes = String.class)
    public synchronized void onRemove(String domain) {
        Map<String, TransactionDao> d = cache.getIfPresent(domain);
        if (d != null) {
            d.values().forEach(transactionDao -> {
                try {
                    transactionDao.close();
                } catch (Exception ignore) {
                }
            });
            cache.invalidate(domain);
        }
    }


    public Set<Cascader> domains() {

        Set<Cascader> sets = new HashSet<>();

        cache.asMap()
                .forEach((domain, transactionDaoMap) -> {

                    Cascader cascader = new Cascader();
                    cascader.label = domain;

                    if (transactionDaoMap.values().stream().anyMatch(item -> item instanceof RedisClusterTransactionDao)) {
                        JedisCluster jedisCluster = transactionDaoMap.values().stream().findFirst().map(item -> ((RedisClusterTransactionDao) item).getCluster()).get();
                        getMasterNodeKeys(jedisCluster).stream().sorted().forEach(row -> {
                            Child child = new Child();
                            child.label = row;
                            cascader.children.add(child);
                        });
                    } else {
                        transactionDaoMap.forEach((row, dao) -> {
                            Child child = new Child();
                            child.label = row;
                            cascader.children.add(child);

                        });
                    }
                    sets.add(cascader);
                });

        return sets;
    }


    @PreDestroy
    public void close() {
        cache.cleanUp();
    }

    public Optional<TransactionDao> get(String domain, String row) {
        Map<String, TransactionDao> ifPresent = cache.getIfPresent(domain);
        if (ifPresent != null) return Optional.ofNullable(ifPresent.get(row));
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Set<String> getMasterNodeKeys(JedisCluster jedisCluster) {
        Set<String> masterNodeKeys = new HashSet<>();
        jedisCluster.getClusterNodes().forEach((key, value) -> {
            try (Jedis jedis = value.getResource()) {

                List<Object> slots = jedis.clusterSlots();

                for (Object slotInfoObj : slots) {
                    List<Object> slotInfo = (List<Object>) slotInfoObj;

                    if (slotInfo.size() <= 2) {
                        continue;
                    }

                    // hostInfos
                    List<Object> hostInfos = (List<Object>) slotInfo.get(2);
                    if (hostInfos.isEmpty()) {
                        continue;
                    }

                    // at this time, we just use master, discard slave information
                    HostAndPort node = generateHostAndPort(hostInfos);

                    masterNodeKeys.add(String.format(KEY_FORMAT, node.getHost(), node.getPort(), 0));
                }
            } catch (Exception e) {
                // try next jedispool
            }
        });
        return masterNodeKeys;
    }

    private HostAndPort generateHostAndPort(List<Object> hostInfos) {
        String host = encode((byte[]) hostInfos.get(0));
        int port = ((Long) hostInfos.get(1)).intValue();
        return new HostAndPort(host, port);
    }

    private String encode(final byte[] data) {
        try {
            return new String(data, Protocol.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new JedisException(e);
        }
    }

    @Data
    public static class Cascader {

        private String label;
        private List<Child> children = new ArrayList<>();


    }

    @Data
    public static class Child {
        private String label;

    }
}
