package org.mengyun.tcctransaction.server.dao;


import org.mengyun.tcctransaction.server.model.Page;
import org.mengyun.tcctransaction.server.vo.TransactionVo;

import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
@Deprecated
public class JdbcTransactionDao implements TransactionDao {

    private static final int IS_DELETE = 1;

    private static final int NOT_DELETE = 0;

    private static final String KEY_NAME_SPACE = "TCC_TRANSACTION";

    private DataSource dataSource;

    private String tableSuffix;

    private String domain;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    private String getTableName() {
        return KEY_NAME_SPACE + "_" + tableSuffix;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    @Override
    public void confirm(String globalTxId, String branchQualifier) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            String sql = "UPDATE " + tableName +
                    " SET STATUS=2" +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("resetRetryCount error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
    }

    @Override
    public void cancel(String globalTxId, String branchQualifier) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            String sql = "UPDATE " + tableName +
                    " SET STATUS=3" +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("resetRetryCount error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
    }

    @Override
    public void delete(String globalTxId, String branchQualifier) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            // 如果记录有删除标记，再次删除执行真正的DELETE
            String sql = "DELETE " + tableName +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ? AND IS_DELETE = " + IS_DELETE;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            preparedStatement.executeUpdate();
            if (preparedStatement.getUpdateCount() == 1) {
                return;
            }

            // 首次删除只设置删除标记，不做真实删除，以后可以恢复记录
            sql = "UPDATE " + tableName +
                    " SET IS_DELETE = " + IS_DELETE +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("delete error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
    }

    @Override
    public void restore(String globalTxId, String branchQualifier) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            String sql = "UPDATE " + tableName +
                    " SET IS_DELETE = " + NOT_DELETE +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("resetRetryCount error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
    }

    @Override
    public void resetRetryCount(String globalTxId, String branchQualifier) {

        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            String sql = "UPDATE " + tableName +
                    " SET RETRIED_COUNT=0" +
                    " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, DatatypeConverter.parseHexBinary(globalTxId));
            preparedStatement.setBytes(2, DatatypeConverter.parseHexBinary(branchQualifier));
            int result = preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("resetRetryCount error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
    }

    @Override
    public Page<TransactionVo> findTransactions(Integer pageNum, int pageSize) {

        Connection connection = getConnection();
        List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();
            String sql = "select DOMAIN," +
                    "GLOBAL_TX_ID," +
                    "BRANCH_QUALIFIER," +
                    "STATUS," +
                    "TRANSACTION_TYPE," +
                    "RETRIED_COUNT," +
                    "CREATE_TIME," +
                    "LAST_UPDATE_TIME from " + tableName + " where IS_DELETE = 0  limit ?,?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, (pageNum - 1) * pageSize);
            preparedStatement.setInt(2, pageSize);

            ResultSet resultSet = preparedStatement.executeQuery();

            buildTransactionVoList(transactionVos, resultSet);
        } catch (Exception e) {
            throw new RuntimeException("findTransactions error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }

        Integer countOfFindTransactions = countOfTransactions();

        return new Page<TransactionVo>(transactionVos, pageNum, pageSize, countOfFindTransactions);

    }

    @Override
    public Page<TransactionVo> findDeletedTransactions(Integer pageNum, int pageSize) {

        Connection connection = getConnection();
        List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();
            String sql = "select DOMAIN," +
                    "GLOBAL_TX_ID," +
                    "BRANCH_QUALIFIER," +
                    "STATUS," +
                    "TRANSACTION_TYPE," +
                    "RETRIED_COUNT," +
                    "CREATE_TIME," +
                    "LAST_UPDATE_TIME from " + tableName + " where IS_DELETE = 1 limit ?,?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, (pageNum - 1) * pageSize);
            preparedStatement.setInt(2, pageSize);

            ResultSet resultSet = preparedStatement.executeQuery();

            buildTransactionVoList(transactionVos, resultSet);
        } catch (Exception e) {
            throw new RuntimeException("findTransactions error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }


        Integer countOfFindTransactions = countOfDeletedTransactions();

        return new Page<TransactionVo>(transactionVos, pageNum, pageSize, countOfFindTransactions);

    }

    private Integer countOfDeletedTransactions() {
        return count(IS_DELETE);
    }

    private Integer countOfTransactions() {
        return count(NOT_DELETE);
    }

    private Integer count(int isDelete) {
        Connection connection = getConnection();
        Page<TransactionVo> pageVo = new Page<TransactionVo>();
        List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
        pageVo.setItems(transactionVos);
        PreparedStatement preparedStatement = null;

        try {
            String tableName = getTableName();

            preparedStatement = connection.prepareStatement("select COUNT(*) as count from " + tableName + " where IS_DELETE = " + isDelete);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (Exception e) {
            throw new RuntimeException("countOfTransactions error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
        return 0;
    }

    private void buildTransactionVoList(List<TransactionVo> transactionVos, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            TransactionVo transactionVo = new TransactionVo();
            transactionVo.setDomain(resultSet.getString(1));
            transactionVo.setGlobalTxId(resultSet.getString(2));
            transactionVo.setBranchQualifier(resultSet.getString(3));
            transactionVo.setStatus(resultSet.getString(4));
            transactionVo.setTransactionType(resultSet.getString(5));
            transactionVo.setRetriedCount(resultSet.getString(6));
            transactionVo.setCreateTime(resultSet.getString(7));
            transactionVo.setLastUpdateTime(resultSet.getString(8));
            transactionVos.add(transactionVo);
        }
    }

    private void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("get jdbc connection error", e);
        }
        return connection;
    }
}
