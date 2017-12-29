package org.mengyun.tcctransaction.server.dao;


import org.mengyun.tcctransaction.server.vo.PageVo;
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
public class JdbcTransactionDao implements TransactionDao {

    private static String KEY_NAME_SPACE = "TCC_TRANSACTION";

    private DataSource dataSource;

    private String tableSuffix;

    private String domain;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    private String getTableName() {
        return KEY_NAME_SPACE + "_" + tableSuffix;
    }

    @Override
    public List<TransactionVo> findTransactions(Integer pageNum, int pageSize) {

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
                    "LAST_UPDATE_TIME from " + tableName + " limit ?,?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, (pageNum - 1) * pageSize);
            preparedStatement.setInt(2, pageSize);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TransactionVo transactionVo = new TransactionVo();
                transactionVo.setDomain(resultSet.getString(1));
                transactionVo.setGlobalTxId(DatatypeConverter.printHexBinary(resultSet.getBytes(2)));
                transactionVo.setBranchQualifier(DatatypeConverter.printHexBinary(resultSet.getBytes(3)));
                transactionVo.setStatus(resultSet.getInt(4));
                transactionVo.setTransactionType(resultSet.getInt(5));
                transactionVo.setRetriedCount(resultSet.getInt(6));
                transactionVo.setCreateTime(resultSet.getTimestamp(7));
                transactionVo.setLastUpdateTime(resultSet.getTimestamp(8));
                transactionVos.add(transactionVo);
            }
        } catch (Exception e) {
            throw new RuntimeException("findTransactions error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
        return transactionVos;
    }

    @Override
    public Integer countOfFindTransactions() {

        Connection connection = getConnection();
        PageVo<TransactionVo> pageVo = new PageVo<TransactionVo>();
        List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
        pageVo.setItems(transactionVos);
        PreparedStatement preparedStatement = null;

        try {
            String tableName = getTableName();

            preparedStatement = connection.prepareStatement("select COUNT(*) as count from " + tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (Exception e) {
            throw new RuntimeException("countOfFindTransactions error", e);
        } finally {
            closeStatement(preparedStatement);
            releaseConnection(connection);
        }
        return 0;
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
    public void delete(String globalTxId, String branchQualifier) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String tableName = getTableName();

            String sql = "DELETE " + tableName +
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
    public String getDomain() {
        return domain;
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


    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }
}
