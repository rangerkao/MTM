package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import common.cache.CacheAction;

public class BaseDao {
	
	Logger logger = CacheAction.logger;
	
	
	public Connection getConn1() throws ClassNotFoundException, SQLException {
		return CacheAction.getConn1();
	}
	public void closeConnection(Connection conn,Statement st,PreparedStatement ps,ResultSet rs) {
		
		try {
			if(st!=null)
				st.close();
		} catch (SQLException e) {}
		
		try {
			if(ps!=null)
				ps.close();
		} catch (SQLException e) {}
	
		try {
			if(conn!=null)
				conn.close();
		} catch (SQLException e) {}
		
		try {
			if(rs!=null)
				rs.close();
		} catch (SQLException e) {}
		
	}

}
