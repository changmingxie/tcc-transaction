package org.mengyun.tcctransaction.repository;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.repository.helper.TransactionSerializer;
import org.mengyun.tcctransaction.serializer.JdkSerializationSerializer;
import org.mengyun.tcctransaction.serializer.ObjectSerializer;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by changming.xie on 2/18/16.
 */
public class ZooKeeperTransactionRepository extends CachableTransactionRepository {

    private String zkServers;

    private int zkTimeout;

    private String zkRootPath = "/tcc";

    private volatile ZooKeeper zk;

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    public ZooKeeperTransactionRepository() {
        super();
    }

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public void setZkRootPath(String zkRootPath) {
        this.zkRootPath = zkRootPath;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public void setZkTimeout(int zkTimeout) {
        this.zkTimeout = zkTimeout;
    }

    @Override
    protected int doCreate(Transaction transaction) {

        try {
            getZk().create(getTxidPath(transaction.getXid()),
                    TransactionSerializer.serialize(serializer, transaction), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return 1;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        try {

            transaction.updateTime();
            transaction.updateVersion();
            Stat stat = getZk().setData(getTxidPath(transaction.getXid()), TransactionSerializer.serialize(serializer, transaction), (int) transaction.getVersion() - 2);
            return 1;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {
        try {
            getZk().delete(getTxidPath(transaction.getXid()), (int) transaction.getVersion() - 1);
            return 1;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        byte[] content = null;
        try {
            Stat stat = new Stat();
            content = getZk().getData(getTxidPath(xid), false, stat);
            Transaction transaction = TransactionSerializer.deserialize(serializer, content);
            return transaction;
        } catch (KeeperException.NoNodeException e) {

        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
        return null;
    }

    @Override
    protected List<Transaction> doFindAllUnmodifiedSince(Date date) {

        List<Transaction> allTransactions = doFindAll();

        List<Transaction> allUnmodifiedSince = new ArrayList<Transaction>();

        for (Transaction transaction : allTransactions) {
            if (transaction.getLastUpdateTime().compareTo(date) < 0) {
                allUnmodifiedSince.add(transaction);
            }
        }

        return allUnmodifiedSince;
    }

    protected List<Transaction> doFindAll() {

        List<Transaction> transactions = new ArrayList<Transaction>();

        List<String> znodePaths = null;
        try {
            znodePaths = getZk().getChildren(zkRootPath, false);
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }

        for (String znodePath : znodePaths) {

            byte[] content = null;
            try {
                Stat stat = new Stat();
                content = getZk().getData(getTxidPath(znodePath), false, stat);
                Transaction transaction =  TransactionSerializer.deserialize(serializer, content);
                transactions.add(transaction);
            } catch (Exception e) {
                throw new TransactionIOException(e);
            }
        }

        return transactions;
    }

    private ZooKeeper getZk() {

        if (zk == null) {
            synchronized (ZooKeeperTransactionRepository.class) {
                if (zk == null) {
                    try {
                        zk = new ZooKeeper(zkServers, zkTimeout, new Watcher() {
                            @Override
                            public void process(WatchedEvent watchedEvent) {

                            }
                        });

                        Stat stat = zk.exists(zkRootPath, false);

                        if (stat == null) {
                            zk.create(zkRootPath, zkRootPath.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        }
                    } catch (Exception e) {
                        throw new TransactionIOException(e);
                    }
                }
            }
        }
        return zk;
    }

    private String getTxidPath(Xid xid) {
        return String.format("%s/%s", zkRootPath, xid);
    }

    private String getTxidPath(String znodePath) {
        return String.format("%s/%s", zkRootPath, znodePath);
    }


}
