package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import common.bean.PermissionBean;

@Repository
public class AdminDao extends BaseDao{
	
	public List<PermissionBean> queryPermission(String account) throws ClassNotFoundException, SQLException{
		
		int level = 10; 
		List<PermissionBean> result = new ArrayList<PermissionBean>();
		
		String sql = "Select user_level from MTM_USER WHERE ACCOUNT = '"+account+"' ";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			logger.info("Query user level : "+sql);
			rs = st.executeQuery(sql);
			
			if(rs.next()) {
				level = rs.getInt("user_level");
			}
			rs = null ;
			
			sql = "select A.ACCOUNT,A.USER_LEVEL,A.OWNER,B.SEARCH,B.SUSPEND,B.RESUME,B.TERMINATE,B.RESET,A.SUBSIDIARYID "
					+ "from MTM_USER A, MTM_PERMISSION B "
					+ "where A.account = B.account AND B.ACCOUNT = '"+account+"' ";
			
			
			logger.info("Query user: "+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				PermissionBean p = new PermissionBean();
				p.setAccount(rs.getString("ACCOUNT"));
				//p.setPassword(rs.getString("PASSWORD"));
				p.setLevel(rs.getInt("USER_LEVEL"));
				p.setOwner(rs.getString("OWNER"));
				p.setSubsidiaryid(rs.getString("SUBSIDIARYID"));
				p.setSearch(1==rs.getInt("SEARCH"));
				p.setResume(1==rs.getInt("RESUME"));
				p.setSuspend(1==rs.getInt("SUSPEND"));
				p.setTerminate(1==rs.getInt("TERMINATE"));
				p.setReset(1==rs.getInt("RESET"));
				result.add(p);
			}
			
			
			sql = ""
					+ "select A.ACCOUNT,A.USER_LEVEL,A.OWNER,B.SEARCH,B.SUSPEND,B.RESUME,B.TERMINATE,B.RESET,A.SUBSIDIARYID "
					+ "from MTM_USER A, MTM_PERMISSION B "
					+ "where A.account = B.account AND B.ACCOUNT <> '"+account+"' ";
			
			if(level>0) {
				sql += "AND B.ACCOUNT in ( "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"' "
						+ "union "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER in (SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"') "
						+ ") ";
			}
			
			sql += "order by A.USER_LEVEL  ";
			rs = null;
			
			logger.info("belong: "+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				PermissionBean p = new PermissionBean();
				p.setAccount(rs.getString("ACCOUNT"));
				//p.setPassword(rs.getString("PASSWORD"));
				p.setLevel(rs.getInt("USER_LEVEL"));
				p.setOwner(rs.getString("OWNER"));
				p.setSubsidiaryid(rs.getString("SUBSIDIARYID"));
				p.setSearch(1==rs.getInt("SEARCH"));
				p.setResume(1==rs.getInt("RESUME"));
				p.setSuspend(1==rs.getInt("SUSPEND"));
				p.setTerminate(1==rs.getInt("TERMINATE"));
				p.setReset(1==rs.getInt("RESET"));
				result.add(p);
			}
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		
		return result;
	}
	
	
	
	public Map<String,String> queryUserList(int level) throws SQLException, ClassNotFoundException{
		Map<String,String> result = new HashMap<String,String>();
		
		String sql = "Select account,SUBSIDIARYID from MTM_USER ";
		
		if(level>=0)
				sql += "where USER_LEVEL = "+level;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			logger.info("quser user list :"+sql);
			rs = st.executeQuery(sql);
			while(rs.next()) {
				result.put(rs.getString("account"),rs.getString("SUBSIDIARYID")==null?" ":rs.getString("SUBSIDIARYID"));
			}
			
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		
		return result;
	}
	
	public String updatePermission(List<String> addItem ,List<String> updateItem , Map<String,PermissionBean> dataMap) throws ClassNotFoundException, SQLException{
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();

			/*//進行新增
			String sql = "INSERT INTO MTM_USER (ACCOUNT,PASSWORD,USER_LEVEL,OWNER,SUBSIDIARYID) "
					+ "VALUES(?,?,?,?,?)";
			
			ps = conn1.prepareStatement(sql);
			for(String account : addItem) {
				PermissionBean p = dataMap.get(account);
				ps.setString(1, p.getAccount());
				ps.setString(2,p.getPassword());
				ps.setInt(3,p.getLevel());
				ps.setString(4, p.getOwner());
				ps.setString(5, p.getSubsidiaryid());
				ps.addBatch();
			}
			logger.info("Execute insert USER batch:");
			ps.executeBatch();
			
			ps=null;
			sql = "INSERT INTO MTM_PERMISSION (ACCOUNT,SEARCH,SUSPEND,RESUME,TERMINATE,RESET) "
					+ "VALUES(?,?,?,?,?)";
			
			ps = conn1.prepareStatement(sql);
			for(String account : addItem) {
				PermissionBean p = dataMap.get(account);
				ps.setString(1, p.getAccount());
				ps.setInt(2,p.getSearch()? 1 : 0 );
				ps.setInt(3,p.getSuspend()? 1 : 0 );
				ps.setInt(4, p.getResume()? 1 : 0 );
				ps.setInt(5, p.getTerminate()? 1 : 0 );
				ps.setInt(6, p.getReset()? 1 : 0 );
				ps.addBatch();
			}
			logger.info("Execute insert permission batch:");
			ps.executeBatch();
			
			//更新
			ps=null;
			sql = "UPDATE MTM_USER SET PASSWORD = ? WHERE ACCOUNT = ? ";
			
			ps = conn1.prepareStatement(sql);
			for(String account : updateItem) {
				PermissionBean p = dataMap.get(account);
				ps.setString(1,p.getPassword());
				ps.setString(2, p.getAccount());
				ps.addBatch();
			}
			logger.info("Execute update USER batch:");
			ps.executeBatch();
			
			ps=null;*/
			String sql = "UPDATE MTM_PERMISSION SET SEARCH=? ,SUSPEND=? ,RESUME=? ,TERMINATE=? ,RESET=? WHERE ACCOUNT = ? ";
			
			ps = conn.prepareStatement(sql);
			for(String account : updateItem) {
				PermissionBean p = dataMap.get(account);
				ps.setInt(1,p.getSearch()? 1 : 0 );
				ps.setInt(2,p.getSuspend()? 1 : 0 );
				ps.setInt(3, p.getResume()? 1 : 0 );
				ps.setInt(4, p.getTerminate()? 1 : 0 );
				ps.setInt(5, p.getReset()? 1 : 0 );
				ps.setString(6, p.getAccount());
				ps.addBatch();
			}
			logger.info("Execute update USER batch:");
			ps.executeBatch();
			
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		
		return "success";
	}
	
	
	public String newAccount(String acc,Map<String,Object> owner) throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			
			String sql = "insert into MTM_USER (ACCOUNT,PASSWORD,USER_LEVEL,OWNER,SUBSIDIARYID) values(?,?,?,?,?)";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, acc);
			ps.setString(2, acc);
			ps.setInt(3, Integer.parseInt((String) owner.get("USER_LEVEL"))+1);
			ps.setString(4, (String) owner.get("ACCOUNT"));
			ps.setString(5, (String) owner.get("SUBSIDIARYIDS"));
			
			
			logger.info("Execute insert USER batch:");
			ps.addBatch();
			ps.executeBatch();
			
			ps = null;
			
			sql = "insert into MTM_PERMISSION(ACCOUNT) values(?) ";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, acc);
			
			
			logger.info("Execute insert PERMISSION batch:");
			ps.addBatch();
			ps.executeBatch();
			
			
			
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		
		return "success";
	}
	
	public String changePassword(String account,String oldPassword,String newPassword) throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "update MTM_USER set PASSWORD = ? where ACCOUNT = ? ";
		if(oldPassword!=null)
				sql += "and PASSWORD = ? ";
		
		try {
			conn = getConn1();
			logger.info("update:" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, newPassword);
			ps.setString(2, account);
			if (oldPassword != null)
				ps.setString(3, oldPassword);
			ps.addBatch();
			int result[] = ps.executeBatch();
			if (result[0] == 0) {
				return "update failed.";
			} 
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		return "success";
	}

}
