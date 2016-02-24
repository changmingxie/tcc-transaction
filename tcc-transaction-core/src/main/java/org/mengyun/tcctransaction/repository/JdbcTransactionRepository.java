package org.mengyun.tcctransaction.repository;


import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.mengyun.tcctransaction.utils.SerializationUtils;

import javax.sql.DataSource;
import javax.transaction.xa.Xid;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by changmingxie on 10/30/15.
 */
public class JdbcTransactionRepository extends CachableTransactionRepository {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected void doCreate(Transaction transaction) {

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO TCC_TRANSACTION " +
                    "(GLOBAL_TX_ID,BRANCH_QUALIFIER,TRANSACTION_TYPE,CONTENT,STATUS,RETRIED_COUNT)" +
                    "VALUES(?,?,?,?,?,0)");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setBytes(1, transaction.getXid().getGlobalTransactionId());
            stmt.setBytes(2, transaction.getXid().getBranchQualifier());

            stmt.setInt(3, transaction.getTransactionType().getId());
            stmt.setBytes(4, SerializationUtils.serialize(transaction));
            stmt.setInt(5, transaction.getStatus().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    protected void doUpdate(Transaction transaction) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE TCC_TRANSACTION SET " +
                    "CONTENT = ?,STATUS = ?,RETRIED_COUNT = ? WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?");

            stmt = connection.prepareStatement(builder.toString());

            stmt.setBytes(1, SerializationUtils.serialize(transaction));
            stmt.setInt(2, transaction.getStatus().getId());
            stmt.setInt(3, transaction.getRetriedCount());
            stmt.setBytes(4, transaction.getXid().getGlobalTransactionId());
            stmt.setBytes(5, transaction.getXid().getBranchQualifier());

            stmt.executeUpdate();

        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    protected void doDelete(Transaction transaction) {
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
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    protected Transaction doFindOne(Xid xid) {

        List<Transaction> transactions = doFind(Arrays.asList(xid));

        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.get(0);
        }
        return null;
    }

    @Override
    protected List<Transaction> doFindAll() {
        return doFind(null);
    }


    protected List<Transaction> doFind(List<Xid> xids) {

        List<Transaction> transactions = new ArrayList<Transaction>();

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("SELECT GLOBAL_TX_ID, BRANCH_QUALIFIER, CONTENT,STATUS,TRANSACTION_TYPE,RETRIED_COUNT" +
                    "  FROM TCC_TRANSACTION ");

            if (!CollectionUtils.isEmpty(xids)) {
                builder.append(" WHERE ");
                for (Xid xid : xids) {
                    builder.append("( GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ? )");
                }
            }

            stmt = connection.prepareStatement(builder.toString());

            if (!CollectionUtils.isEmpty(xids)) {

                int i = 0;

                for (Xid xid : xids) {
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
                int retriedCount = resultSet.getInt(6);

                Transaction transaction = (Transaction) SerializationUtils.deserialize(transactionBytes);
                transaction.resetRetriedCount(retriedCount);

                transactions.add(transaction);
            }
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return transactions;
    }


    protected Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    protected void releaseConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    private void closeStatement(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (Exception ex) {
            throw new TransactionIOException(ex);
        }
    }
}
