package org.mengyun.tcctransaction.storage;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.api.Xid;
import org.mengyun.tcctransaction.serializer.TransactionStoreSerializer;
import org.mengyun.tcctransaction.storage.domain.AlertType;
import org.mengyun.tcctransaction.storage.domain.DomainStore;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.mengyun.tcctransaction.xid.TransactionXid;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by changmingxie on 10/30/15.
 */
public class JdbcTransactionStorage extends AbstractTransactionStorage implements StorageRecoverable {

    private static final int MARK_DELETED_YES = 1;
    private static final int MARK_DELETED_NO = 0;

    private static final String SQL_SELECT_PREFIX_FOR_TCC_TRANSACTION = "SELECT DOMAIN,ROOT_XID,XID,CONTENT,STATUS,TRANSACTION_TYPE,CREATE_TIME,LAST_UPDATE_TIME,RETRIED_COUNT,VERSION,IS_DELETE,ROOT_DOMAIN,REQUEST_ID,ID FROM ";
    private static final String SQL_SELECT_PREFIX_FOR_TCC_DOMAIN = "SELECT DOMAIN, MAX_RETRY_COUNT, MAX_RECOVERY_REQUEST_PER_SECOND,  PHONE_NUMBERS, ALERT_TYPE, THRESHOLD, REACH_LIMIT_THRESHOLD, INTERVAL_MINUTES,LAST_ALERT_TIME,DING_ROBOT_URL,CREATE_TIME,LAST_UPDATE_TIME,VERSION FROM ";

    private String tbSuffix;
    private DataSource dataSource;

    public JdbcTransactionStorage(TransactionStoreSerializer serializer, StoreConfig storeConfig) {
        super(serializer, storeConfig);
        this.tbSuffix = storeConfig.getTbSuffix();
        this.dataSource = storeConfig.getDataSource();
    }

    public String getTbSuffix() {
        return tbSuffix;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected int doCreate(TransactionStore transactionStore) {

        if (doFindOne(transactionStore.getDomain(), transactionStore.getXid(), false) != null) {
            return 0;
        }

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder
                    .append("INSERT INTO ")
                    .append(getTableName())
                    .append("(ROOT_XID,XID,TRANSACTION_TYPE,CONTENT,STATUS,RETRIED_COUNT,CREATE_TIME,LAST_UPDATE_TIME,VERSION,ROOT_DOMAIN,DOMAIN,REQUEST_ID,ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            stmt = connection.prepareStatement(builder.toString());
            int parameterIndex = 1;
            stmt.setString(parameterIndex++, transactionStore.getRootXid().toString());
            stmt.setString(parameterIndex++, transactionStore.getXid().toString());
            stmt.setInt(parameterIndex++, transactionStore.getTransactionTypeId());
            stmt.setBytes(parameterIndex++, transactionStore.getContent());
            stmt.setInt(parameterIndex++, transactionStore.getStatusId());
            stmt.setInt(parameterIndex++, transactionStore.getRetriedCount());
            stmt.setTimestamp(parameterIndex++, new Timestamp(transactionStore.getCreateTime().getTime()));
            stmt.setTimestamp(parameterIndex++, new Timestamp(transactionStore.getLastUpdateTime().getTime()));
            stmt.setLong(parameterIndex++, transactionStore.getVersion());
            stmt.setString(parameterIndex++, transactionStore.getRootDomain());
            stmt.setString(parameterIndex++, transactionStore.getDomain());
            if (transactionStore.getRequestId() != null) {
                stmt.setInt(parameterIndex++, transactionStore.getRequestId());
            } else {
                stmt.setNull(parameterIndex++, Types.INTEGER);
            }
            if (transactionStore.getId() != null) {
                stmt.setLong(parameterIndex++, transactionStore.getId());
            } else {
                stmt.setNull(parameterIndex++, Types.BIGINT);
            }
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected int doUpdate(TransactionStore transactionStore) {
        Connection connection = null;
        PreparedStatement stmt = null;


        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE " + getTableName() + " SET " +
                    "CONTENT = ?,STATUS = ?,REQUEST_ID = ?,LAST_UPDATE_TIME = ?, RETRIED_COUNT = ?,VERSION = VERSION+1 WHERE XID = ? AND VERSION = ?");

            builder.append(StringUtils.isNotEmpty(transactionStore.getDomain()) ? " AND DOMAIN = ?" : "");
            builder.append(transactionStore.getId() != null ? " AND ID = ?" : "");
            stmt = connection.prepareStatement(builder.toString());

            int parameterIndex = 1;
            stmt.setBytes(parameterIndex++, transactionStore.getContent());
            stmt.setInt(parameterIndex++, transactionStore.getStatusId());
            if (transactionStore.getRequestId() != null) {
                stmt.setInt(parameterIndex++, transactionStore.getRequestId());
            } else {
                stmt.setNull(parameterIndex++, Types.INTEGER);
            }
            stmt.setTimestamp(parameterIndex++, new Timestamp(transactionStore.getLastUpdateTime().getTime()));

            stmt.setInt(parameterIndex++, transactionStore.getRetriedCount());
            stmt.setString(parameterIndex++, transactionStore.getXid().toString());
            stmt.setLong(parameterIndex++, transactionStore.getVersion() - 1);

            if (StringUtils.isNotEmpty(transactionStore.getDomain())) {
                stmt.setString(parameterIndex++, transactionStore.getDomain());
            }

            if (transactionStore.getId() != null) {
                stmt.setLong(parameterIndex++, transactionStore.getId());
            }

            return stmt.executeUpdate();

        } catch (Throwable e) {
            throw new TransactionIOException(e);
        }finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected int doDelete(TransactionStore transactionStore) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM " + getTableName() +
                    " WHERE XID = ? AND VERSION =?");

            builder.append(StringUtils.isNotEmpty(transactionStore.getDomain()) ? " AND DOMAIN = ?" : "");
            builder.append(transactionStore.getId()!=null ? " AND ID = ?" : "");

            stmt = connection.prepareStatement(builder.toString());
            int parameterIndex = 1;
            stmt.setString(parameterIndex++, transactionStore.getXid().toString());
            stmt.setLong(parameterIndex++, transactionStore.getVersion());

            if (StringUtils.isNotEmpty(transactionStore.getDomain())) {
                stmt.setString(parameterIndex++, transactionStore.getDomain());
            }
            if (transactionStore.getId() != null) {
                stmt.setLong(parameterIndex++, transactionStore.getId());
            }

            stmt.executeUpdate();
            return 1;

        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected int doMarkDeleted(TransactionStore transactionStore) {
        return doMarkDeleted(transactionStore, true);
    }

    @Override
    protected int doRestore(TransactionStore transactionStore) {
        return doMarkDeleted(transactionStore, false);
    }

    @Override
    protected int doCompletelyDelete(TransactionStore transactionStore) {
        return doDelete(transactionStore);
    }

    private int doMarkDeleted(TransactionStore transactionStore, boolean isMarkDeleted) {
        Connection connection = null;
        PreparedStatement stmt = null;

        Date lastUpdateTime = transactionStore.getLastUpdateTime();
        long currentVersion = transactionStore.getVersion();

        transactionStore.setLastUpdateTime(new Date());
        transactionStore.setVersion(transactionStore.getVersion() + 1);

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE " + getTableName() + " SET " +
                    "IS_DELETE = ?,LAST_UPDATE_TIME = ?, VERSION = VERSION+1 WHERE XID = ? AND VERSION = ?");

            builder.append(StringUtils.isNotEmpty(transactionStore.getDomain()) ? " AND DOMAIN = ?" : "");

            builder.append(transactionStore.getId()!=null ? " AND ID = ?" : "");

            stmt = connection.prepareStatement(builder.toString());

            int parameterIndex = 1;
            stmt.setInt(parameterIndex++, isMarkDeleted ? MARK_DELETED_YES : MARK_DELETED_NO);
            stmt.setTimestamp(parameterIndex++, new Timestamp(transactionStore.getLastUpdateTime().getTime()));

            stmt.setString(parameterIndex++, transactionStore.getXid().toString());
            stmt.setLong(parameterIndex++, currentVersion);

            if (StringUtils.isNotEmpty(transactionStore.getDomain())) {
                stmt.setString(parameterIndex++, transactionStore.getDomain());
            }
            if (transactionStore.getId() != null) {
                stmt.setLong(parameterIndex++, transactionStore.getId());
            }
            int result = stmt.executeUpdate();

            return result;

        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            transactionStore.setLastUpdateTime(lastUpdateTime);
            transactionStore.setVersion(currentVersion);
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    protected TransactionStore doFindOne(String domain, Xid xid, boolean isMarkDeleted) {
        return doFind(domain, xid, isMarkDeleted);
    }

    @Override
    public Page<TransactionStore> findAllUnmodifiedSince(String domain, Date date, String offset, int pageSize) {
        return pageList(domain, date, offset, pageSize, false);
    }

    @Override
    public Page<TransactionStore> findAllDeletedSince(String domain, Date date, String offset, int pageSize) {
        return pageList(domain, date, offset, pageSize, true);
    }

    @Override
    public int count(String domain, boolean isMarkDeleted) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            connection = this.getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT COUNT(1)");
            builder.append("  FROM " + getTableName() + " WHERE  DOMAIN = ? AND IS_DELETE = ?");
            stmt = connection.prepareStatement(builder.toString());
            stmt.setString(1, domain);
            stmt.setInt(2, isMarkDeleted ? MARK_DELETED_YES : MARK_DELETED_NO);

            resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception ignore) {
                //ignore
            }
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    @Override
    public void registerDomain(DomainStore domainStore) {

        DomainStore record = findDomain(domainStore.getDomain());
        if (record != null) {
            return;
        }

        domainStore.setVersion(1);
        executeUpdate(connection -> {

            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO " + getDomainTableName() +
                    "(DOMAIN,PHONE_NUMBERS,ALERT_TYPE,THRESHOLD,INTERVAL_MINUTES,LAST_ALERT_TIME,DING_ROBOT_URL,CREATE_TIME,LAST_UPDATE_TIME,VERSION)");
            builder.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");

            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            stmt.setString(1, domainStore.getDomain());
            stmt.setString(2, domainStore.getPhoneNumbers());
            stmt.setString(3, domainStore.getAlertType() == null ? null : domainStore.getAlertType().name());
            stmt.setInt(4, domainStore.getThreshold());
            stmt.setInt(5, domainStore.getIntervalMinutes());
            stmt.setTimestamp(6, domainStore.getLastAlertTime() == null ? null : new Timestamp(domainStore.getLastAlertTime().getTime()));
            stmt.setString(7, domainStore.getDingRobotUrl());
            stmt.setTimestamp(8, new Timestamp(domainStore.getCreateTime().getTime()));
            stmt.setTimestamp(9, new Timestamp(domainStore.getLastUpdateTime().getTime()));
            stmt.setLong(10, domainStore.getVersion());

            return stmt;
        });
    }

    @Override
    public void updateDomain(DomainStore domainStore) {
        executeUpdate(connection -> {

            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE " + getDomainTableName() + " SET " +
                    "MAX_RETRY_COUNT = ?,MAX_RECOVERY_REQUEST_PER_SECOND = ?,PHONE_NUMBERS = ?," +
                    "ALERT_TYPE = ?," +
                    "THRESHOLD = ?," +
                    "REACH_LIMIT_THRESHOLD = ?," +
                    "INTERVAL_MINUTES = ?," +
                    "LAST_ALERT_TIME = ?," +
                    "DING_ROBOT_URL = ?," +
                    "LAST_UPDATE_TIME = ?, VERSION = VERSION+1 WHERE DOMAIN = ? AND VERSION = ?");

            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            stmt.setInt(1,domainStore.getMaxRetryCount());
            stmt.setInt(2,domainStore.getMaxRecoveryRequestPerSecond());
            stmt.setString(3, domainStore.getPhoneNumbers());
            stmt.setString(4, domainStore.getAlertType().name());
            stmt.setInt(5, domainStore.getThreshold());
            stmt.setInt(6, domainStore.getReachLimitThreshold());
            stmt.setInt(7, domainStore.getIntervalMinutes());
            stmt.setTimestamp(8, domainStore.getLastAlertTime() == null ? null : new Timestamp(domainStore.getLastAlertTime().getTime()));
            stmt.setString(9, domainStore.getDingRobotUrl());
            stmt.setTimestamp(10, new Timestamp(domainStore.getLastUpdateTime().getTime()));
            stmt.setString(11, domainStore.getDomain());
            stmt.setLong(12, domainStore.getVersion());

            return stmt;
        });
    }

    @Override
    public void removeDomain(String domain) {
        executeUpdate(connection -> {

            StringBuilder builder = new StringBuilder();
            builder.append("DELETE FROM " + getDomainTableName() +
                    " WHERE DOMAIN = ?");

            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            stmt.setString(1, domain);

            return stmt;
        });
    }

    @Override
    public DomainStore findDomain(String domain) {

        List<DomainStore> list = executeDomainQuery(connection -> {
            StringBuilder builder = new StringBuilder();
            builder.append(SQL_SELECT_PREFIX_FOR_TCC_DOMAIN + getDomainTableName() + " WHERE DOMAIN = ?");

            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            stmt.setString(1, domain);
            return stmt;
        });

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<DomainStore> getAllDomains() {
        return executeDomainQuery(connection -> {
            StringBuilder builder = new StringBuilder();
            builder.append(SQL_SELECT_PREFIX_FOR_TCC_DOMAIN + getDomainTableName());
            PreparedStatement stmt = connection.prepareStatement(builder.toString());
            return stmt;
        });
    }

    @Override
    public boolean supportStorageRecoverable() {
        return true;
    }

    private List<DomainStore> executeDomainQuery(StatementBuilder builder) {
        return executeQuery(builder, this::constructDomains);
    }

    private Page<TransactionStore> pageList(String domain, Date date, String offset, int pageSize, boolean isMarkDeleted) {

        List<TransactionStore> transactions = new ArrayList<>();

        Connection connection = null;
        PreparedStatement stmt = null;

        int currentOffset = StringUtils.isEmpty(offset) ? 0 : Integer.parseInt(offset);

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append(SQL_SELECT_PREFIX_FOR_TCC_TRANSACTION + getTableName() + " WHERE LAST_UPDATE_TIME < ?");
            builder.append(" AND IS_DELETE = ?");
            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");
            builder.append(" ORDER BY TRANSACTION_ID ASC");
            builder.append(String.format(" LIMIT %s, %d", currentOffset, pageSize));

            stmt = connection.prepareStatement(builder.toString());

            stmt.setTimestamp(1, new Timestamp(date.getTime()));
            stmt.setInt(2, isMarkDeleted ? MARK_DELETED_YES : MARK_DELETED_NO);

            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(3, domain);
            }

            ResultSet resultSet = stmt.executeQuery();

            this.constructTransactions(resultSet, transactions);
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return new Page<>(String.valueOf(currentOffset + transactions.size()), transactions);
    }

    private TransactionStore doFind(String domain, Xid xid, boolean isMarkDeleted) {

        List<TransactionStore> transactions = doFinds(domain, Arrays.asList(xid), isMarkDeleted);

        if (!CollectionUtils.isEmpty(transactions)) {
            return transactions.get(0);
        }
        return null;
    }

    private List<TransactionStore> doFinds(String domain, List<Xid> xids, boolean isMarkDeleted) {

        List<TransactionStore> transactions = new ArrayList<>();

        if (CollectionUtils.isEmpty(xids)) {
            return transactions;
        }

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            StringBuilder builder = new StringBuilder();
            builder.append(SQL_SELECT_PREFIX_FOR_TCC_TRANSACTION + getTableName() + " WHERE");

            if (!CollectionUtils.isEmpty(xids)) {
                for (Xid xid : xids) {
                    builder.append(" ( XID = ? ) OR");
                }
                builder.delete(builder.length() - 2, builder.length());
            }

            builder.append(StringUtils.isNotEmpty(domain) ? " AND DOMAIN = ?" : "");
            builder.append(" AND IS_DELETE = ?");

            stmt = connection.prepareStatement(builder.toString());

            int i = 0;

            for (Xid xid : xids) {
                stmt.setString(++i, xid.toString());
            }

            if (StringUtils.isNotEmpty(domain)) {
                stmt.setString(++i, domain);
            }

            stmt.setInt(++i, isMarkDeleted ? MARK_DELETED_YES : MARK_DELETED_NO);
            ResultSet resultSet = stmt.executeQuery();

            this.constructTransactions(resultSet, transactions);
        } catch (Throwable e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }

        return transactions;
    }

    private void constructTransactions(ResultSet resultSet, List<TransactionStore> transactions) throws SQLException {
        while (resultSet.next()) {
            TransactionStore transactionStore = new TransactionStore();
            transactionStore.setDomain(resultSet.getString(1));
            transactionStore.setRootXid(new TransactionXid(resultSet.getString(2)));
            transactionStore.setXid(new TransactionXid(resultSet.getString(3)));
            transactionStore.setContent(resultSet.getBytes(4));
            transactionStore.setStatusId(resultSet.getInt(5));
            transactionStore.setTransactionTypeId(resultSet.getInt(6));
            transactionStore.setCreateTime(resultSet.getTimestamp(7));
            transactionStore.setLastUpdateTime(resultSet.getTimestamp(8));
            transactionStore.setRetriedCount(resultSet.getInt(9));
            transactionStore.setVersion(resultSet.getLong(10));
            transactionStore.setRootDomain(resultSet.getString("ROOT_DOMAIN"));
            transactionStore.setRequestId((Integer) resultSet.getObject("REQUEST_ID"));
            transactionStore.setId((Long) resultSet.getObject("ID"));
            transactions.add(transactionStore);
        }
    }

    /**
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private List<DomainStore> constructDomains(ResultSet resultSet) throws SQLException {
        List<DomainStore> domainStoreList = new ArrayList<>();
        while (resultSet.next()) {
            DomainStore domainStore = new DomainStore();
            domainStore.setDomain(resultSet.getString(1));
            domainStore.setMaxRetryCount(resultSet.getInt(2));
            domainStore.setMaxRecoveryRequestPerSecond(resultSet.getInt(3));
            domainStore.setPhoneNumbers(resultSet.getString(4));
            domainStore.setAlertType(AlertType.nameOf(resultSet.getString(5)));
            domainStore.setThreshold(resultSet.getInt(6));
            domainStore.setReachLimitThreshold(resultSet.getInt(7));
            domainStore.setIntervalMinutes(resultSet.getInt(8));
            domainStore.setLastAlertTime(resultSet.getDate(9));
            domainStore.setDingRobotUrl(resultSet.getString(10));
            domainStore.setCreateTime(resultSet.getTimestamp(11));
            domainStore.setLastUpdateTime(resultSet.getTimestamp(12));
            domainStore.setVersion(resultSet.getLong(13));
            domainStoreList.add(domainStore);
        }
        return domainStoreList;
    }

    private Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new TransactionIOException(e);
        }
    }

    private void releaseConnection(Connection con) {
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

    private String getTableName() {
        return StringUtils.isNotEmpty(tbSuffix) ? "TCC_TRANSACTION_" + tbSuffix : "TCC_TRANSACTION";
    }

    private String getDomainTableName() {
        return StringUtils.isNotEmpty(tbSuffix) ? "TCC_DOMAIN_" + tbSuffix : "TCC_DOMAIN";
    }

    private int executeUpdate(StatementBuilder builder) {

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            stmt = builder.build(connection);

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }

    private <T> List<T> executeQuery(StatementBuilder builder, ResultSetConvertor<T> resultSetConvertor) {

        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = this.getConnection();

            stmt = builder.build(connection);

            ResultSet resultSet = stmt.executeQuery();

            return resultSetConvertor.convert(resultSet);

        } catch (SQLException e) {
            throw new TransactionIOException(e);
        } finally {
            closeStatement(stmt);
            this.releaseConnection(connection);
        }
    }


    private interface StatementBuilder {
        PreparedStatement build(Connection connection) throws SQLException;
    }

    private interface ResultSetConvertor<T> {
        List<T> convert(ResultSet resultSet) throws SQLException;
    }
}
