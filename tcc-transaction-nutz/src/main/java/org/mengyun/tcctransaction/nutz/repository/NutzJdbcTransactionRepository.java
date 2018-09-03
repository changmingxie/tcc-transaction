package org.mengyun.tcctransaction.nutz.repository;


import java.sql.Connection;

import org.mengyun.tcctransaction.repository.JdbcTransactionRepository;

/**
 * Created by changmingxie on 10/30/15.
 */
public class NutzJdbcTransactionRepository extends JdbcTransactionRepository {

    protected Connection getConnection() {
    	try {
    		return this.getDataSource().getConnection();
		} catch (Exception e) {
			throw new RuntimeException("获取连接失败",e);
		}
    }

    protected void releaseConnection(Connection con) {
    	try {
    		if(con != null){
        		con.commit();
        		con.close();
        	}
		} catch (Exception e) {
			throw new RuntimeException("关闭连接失败",e);
		}
    }
}
