package org.mengyun.tcctransaction.ha.registry;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mengyun.tcctransaction.ha.registry.jdbc.JDBCNode;
import org.mengyun.tcctransaction.ha.registry.jdbc.JDBCRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisClusterRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisNode;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisRegistration;
import org.mengyun.tcctransaction.ha.registry.jedis.JedisShardedRegistration;
import org.mengyun.tcctransaction.repository.*;
import redis.clients.jedis.*;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Lee on 2020/9/15 14:02.
 * aggregate-framework
 */
public class RegistrationUtil {

    private static final Field PASSWORD;

    static {
        try {
            PASSWORD = BinaryClient.class.getDeclaredField("password");
            PASSWORD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public static Registration get(TransactionRepository repository) {
        if (repository == null) return null;
        if (repository instanceof RedisTransactionRepository) return get((RedisTransactionRepository) repository);
        if (repository instanceof ShardJedisTransactionRepository)
            return get((ShardJedisTransactionRepository) repository);
        if (repository instanceof JedisClusterTransactionRepository)
            return get((JedisClusterTransactionRepository) repository);
        if (repository instanceof JdbcTransactionRepository) return get((JdbcTransactionRepository) repository);
        if (repository instanceof SentinelTransactionRepository)
            return get(((SentinelTransactionRepository) repository).getWorkTransactionRepository());
        return null;

    }


    protected static Registration get(RedisTransactionRepository repository) {

        JedisNode node = getNode(repository.getJedisPool());
        JedisRegistration registration = new JedisRegistration();
        registration.setNode(node);
        registration.setDomain(repository.getDomain());
        return registration;

    }


    protected static Registration get(ShardJedisTransactionRepository repository) {
        try (ShardedJedis resource = repository.getShardedJedisPool().getResource()) {
            Set<JedisNode> collect = resource.getAllShardInfo()
                    .stream()
                    .map(new Function<JedisShardInfo, JedisNode>() {
                        @Override
                        public JedisNode apply(JedisShardInfo info) {
                            JedisNode node = new JedisNode();
                            node.setHost(info.getHost());
                            node.setPassword(info.getPassword());
                            node.setPort(info.getPort());
                            node.setDatabase(0);
                            return node;
                        }
                    })
                    .collect(Collectors.toSet());

            JedisShardedRegistration registration = new JedisShardedRegistration();
            registration.setNodes(collect);
            registration.setDomain(repository.getDomain());
            return registration;
        }
    }

    protected static Registration get(JedisClusterTransactionRepository repository) {
        JedisCluster cluster = repository.getJedisCluster();
        Map<String, JedisPool> nodes = cluster.getClusterNodes();

        Set<JedisNode> collect = nodes.values()
                .stream()
                .map(RegistrationUtil::getNode)
                .collect(Collectors.toSet());

        JedisClusterRegistration registration = new JedisClusterRegistration();
        registration.setNodes(collect);
        registration.setDomain(repository.getDomain());
        return registration;
    }

    protected static Registration get(JdbcTransactionRepository repository) {

        DataSource dataSource = repository.getDataSource();

        try (java.sql.Connection connection = dataSource.getConnection()) {

            Properties clientInfo = connection.getClientInfo();

            DatabaseMetaData metaData = connection.getMetaData();

            String url = metaData.getURL();
            String username = metaData.getUserName();

            JDBCRegistration registration = new JDBCRegistration();

            JDBCNode jdbcNode = new JDBCNode();
            jdbcNode.setUrl(url);
            jdbcNode.setUsername(username);
            jdbcNode.setPassword(clientInfo.getProperty("password"));
            registration.setNode(jdbcNode);
            registration.setDomain(repository.getDomain());
            return registration;

        } catch (SQLException ex) {

            return ExceptionUtils.rethrow(ex);
        }


    }

    private static JedisNode getNode(JedisPool pool) {
        try (Jedis jedis = pool.getResource()) {
            jedis.ping();

            Client client = jedis.getClient();
            String host = client.getHost();
            int database = client.getDB();
            int port = client.getPort();
            String password = (String) PASSWORD.get(client);

            JedisNode node = new JedisNode();
            node.setDatabase(database);
            node.setHost(host);
            node.setPassword(password);
            node.setPort(port);
            return node;
        } catch (Exception ex) {
            return ExceptionUtils.rethrow(ex);
        }
    }
}
