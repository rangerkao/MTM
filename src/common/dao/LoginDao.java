package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDao extends BaseDao{
	
	public Map<String,String>  login(String acc,String pass) throws SQLException, ClassNotFoundException{
		
		Map<String,String> result = new HashMap<String,String>();
		
		String sql = "select A.USER_LEVEL,B.SEARCH,B.RESUME,B.SUSPEND,B.TERMINATE,A.SUBSIDIARYID "
				+ "from MTM_USER A,MTM_PERMISSION B "
				+ "where A.ACCOUNT = B.ACCOUNT "
				+ "and A.ACCOUNT = ? and A.PASSWORD = ? ";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, pass);
			logger.info("select A.USER_LEVEL,B.SEARCH,B.RESUME,B.SUSPEND,B.TERMINATE,B.RESET " + "from MTM_USER A,MTM_PERMISSION B " + "where A.ACCOUNT = B.ACCOUNT "
					+ "and A.ACCOUNT = '" + acc + "' and A.PASSWORD = '" + pass + "' ");
			rs = ps.executeQuery();
			if (rs.next()) {
				result.put("USER_LEVEL", rs.getString("USER_LEVEL"));
				result.put("SEARCH", rs.getString("SEARCH"));
				result.put("RESUME", rs.getString("RESUME"));
				result.put("SUSPEND", rs.getString("SUSPEND"));
				result.put("TERMINATE", rs.getString("TERMINATE"));
				result.put("RESET", rs.getString("RESET"));
				result.put("SUBSIDIARYIDS", rs.getString("SUBSIDIARYID"));
			} 
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}

}
