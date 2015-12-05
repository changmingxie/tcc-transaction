package org.mengyun.tcctransaction.spring;


import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.utils.SerializationUtils;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.CollectionUtils;

import javax.transaction.xa.Xid;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/30/15.
 */
public class JdbcTransactionRepository extends JdbcDaoSupport implements TransactionRepository {

    private final Map<Xid, Transaction> transactionXidCompensableTransactionMap = new ConcurrentHashMap<Xid, Transaction>();

    private final Map<Xid, Transaction> errorTransactionXidCompensableTransactionMap = new ConcurrentHashMap<Xid, Transaction>();

    @Override
    public void create(Transaction transaction) {
        doCreate(transaction);
        transactionXidCompensableTransactionMap.put(transaction.getXid(), transaction);
    }

    @Override
    public void update(Transaction transaction) {
        doUpdate(transaction);
        transactionXidCompensableTransactionMap.put(transaction.getXid(), transaction);
    }

    @Override
    public void delete(Transaction transaction) {
        doDelete(transaction);
        transactionXidCompensableTransactionMap.remove(transaction.getXid());
    }

    @Override
    public Transaction findByXid(TransactionXid transactionXid) {
        Transaction transaction = transactionXidCompensableTransactionMap.get(transactionXid);

        if (transaction == null) {
            transaction = doFind(transactionXid);

            if (transaction == null) {
                throw new Error(String.format("cannot doFind transaction, the xid GTX_ID:%s, BRANCH_ID:%s",
                        transactionXid.getGlobalTransactionId(),
                        transactionXid.getBranchQualifier()));
            }
            transactionXidCompensableTransactionMap.put(transactionXid, transaction);
        }

        return transaction;
    }

    @Override
    public List<Transaction> findAll() {

        List<Transaction> transactions = doFindAll(null);
        for (Transaction transaction : transactions) {
            transactionXidCompensableTransactionMap.put(transaction.getXid(), transaction);
        }

        return transactions;
    }

    @Override
    public void addErrorTransaction(Transaction transaction) {
        errorTransactionXidCompensableTransactionMap.put(transaction.getXid(), transaction);
    }

    @Override
    public void removeErrorTransaction(Transaction transaction) {
        errorTransactionXidCompensableTransactionMap.remove(transaction.getXid());
    }

    @Override
    public Collection<Transaction> findAllErrorTransactions() {
        return errorTransactionXidCompensableTransactionMap.values();
    }

    private void doCreate(Transaction transaction) {

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO TCC_TRANSACTION " +
                    "(GLOBAL_TX_ID,BRANCH_QUALIFIER,TRANSACTION_TYPE,CONTENT,STATUS)" +
                    "VALUES(?,?,?,?,?)");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setBytes(1, transaction.getXid().getGlobalTransactionId());
            stmt.setBytes(2, transaction.getXid().getBranchQualifier());

            stmt.setInt(3, transaction.getTransactionType().getId());
            stmt.setBytes(4, SerializationUtils.serialize(transaction));
            stmt.setInt(5, transaction.getStatus().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new Error(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    private void doUpdate(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE TCC_TRANSACTION SET " +
                    "CONTENT = ?,STATUS = ? WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setBytes(1, SerializationUtils.serialize(transaction));
            stmt.setInt(2, transaction.getStatus().getId());
            stmt.setBytes(3, transaction.getXid().getGlobalTransactionId());
            stmt.setBytes(4, transaction.getXid().getBranchQualifier());

            stmt.executeUpdate();

        } catch (Throwable e) {
            throw new Error(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    public void doDelete(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM TCC_TRANSACTION " +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setBytes(1, transaction.getXid().getGlobalTransactionId());
            stmt.setBytes(2, transaction.getXid().getBranchQualifier());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new Error(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    private Transaction doFind(TransactionXid xid) {

        List<TransactionXid> transactionXids = Arrays.asList(xid);

        List<Transaction> transactions = doFindAll(transactionXids);

        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.get(0);
        }
        return null;
    }

    private List<Transaction> doFindAll(List<TransactionXid> xids) {

        List<Transaction> transactions = new ArrayList<Transaction>();

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("SELECT GLOBAL_TX_ID, BRANCH_QUALIFIER, CONTENT,STATUS,TRANSACTION_TYPE" +
                    "  FROM TCC_TRANSACTION ");

            if (!CollectionUtils.isEmpty(xids)) {
                builder.append(" WHERE ");
                for (TransactionXid xid : xids) {
                    builder.append("( GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ? )");
                }
            }

            stmt = connection.prepareStatement(builder.toString());

            if (!CollectionUtils.isEmpty(xids)) {

                int i = 0;

                for (TransactionXid xid : xids) {
                    stmt.setBytes(++i, xid.getGlobalTransactionId());
                    stmt.setBytes(++i, xid.getBranchQualifier());
                }
            }

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                byte[] globalTxid = resultSet.getBytes(1);
                byte[] branchQualifier = resultSet.getBytes(2);
                byte[] transactionBytes = resultSet.getBytes(3);
                int status = resultSet.getInt(4);
                int transactionType = resultSet.getInt(5);

                Transaction transaction = (Transaction) SerializationUtils.deserialize(transactionBytes);

                transactions.add(transaction);
            }
        } catch (Throwable e) {
            throw new Error(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return transactions;
    }

    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
