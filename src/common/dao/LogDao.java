package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import common.bean.LogBean;

@Repository
public class LogDao extends BaseDao {
	

	public Map<String,String>  login(String acc,String pass) throws SQLException, ClassNotFoundException{
		
		Map<String,String> result = new HashMap<String,String>();
		
		String sql = "select A.ACCOUNT,A.USER_LEVEL,B.SEARCH,B.RESUME,B.SUSPEND,B.TERMINATE,B.RESET,A.SUBSIDIARYID " 
				+ "from MTM_USER A,MTM_PERMISSION B " + "where A.ACCOUNT = B.ACCOUNT "
				+ "and A.ACCOUNT = '" + acc + "' and A.PASSWORD = '" + pass + "' ";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			logger.info(sql);
			rs = st.executeQuery(sql);
			if (rs.next()) {
				result.put("ACCOUNT", rs.getString("ACCOUNT"));
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
	
	
	public String processCondition(Map<String,Object> condition) throws ParseException {
		
		String sCondition=" ";
		if(condition.size()>0) {
			for(String key:condition.keySet()) {
				if("start".equalsIgnoreCase(key)) {
					sCondition += "AND to_char(A.CREATETIME,'yyyyMMdd') >= '"+condition.get(key).toString().replaceAll("/", "")+"' ";//START C
				}else if("end".equalsIgnoreCase(key)) {
					DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					Date endDate = sdf.parse((String)condition.get(key));
					sCondition += "AND to_char(A.CREATETIME,'yyyyMMdd') < '"+sdf.format(endDate.getTime()+24*60*60*1000).replaceAll("/", "")+"' ";//END C
				}
			}
		}
		
		return sCondition;
	}

	public List<LogBean> queryLogDatas(int startPage, int onePage, String orderby, Boolean asc, Map<String, Object> condition) throws SQLException, ClassNotFoundException, ParseException {
		List<LogBean> result = new ArrayList<LogBean>();
		
		if(orderby ==null || "".equals(orderby)) {
			orderby = "A.CREATETIME ";
			asc = false;
		}else if("time".equalsIgnoreCase(orderby)) {
			orderby = "A.CREATETIME ";
		}
		else if("account".equalsIgnoreCase(orderby)) {
			orderby = "A.Account ";
		}
		
		String account = (String) condition.get("account");
		
		String sql = "Select user_level from MTM_USER WHERE ACCOUNT = '"+account+"' ";
		int level = 10;
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
			
			String conditionString = processCondition(condition);
			
			if(level>0) {
				conditionString += "AND A.ACCOUNT in ( "
						+ "SELECT '"+account+"' from dual "
						+ "union "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"' "
						+ "union "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER in (SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"') "
						+ ") ";
			}
			
			sql = ""
					+ "select ACCOUNT,ACTION,PARAMS,TIME "
					+ "from ("
					+ "select A.ACCOUNT,A.ACTION,A.PARAMS,to_char(A.CREATETIME,'yyyy/MM/dd hh24:mi:ss') TIME"
					+ "			,ROW_NUMBER() OVER(ORDER BY "+orderby+" "+(asc?"ASC":"DESC")+") ROW_NUM  "  
					+ "from MTM_ACTION_LOG A,MTM_USER B "
					+ "where A.ACCOUNT = B.ACCOUNT "
					+ conditionString
					+ ")"
					+ "where 1=1 "
					+ (onePage >=0 ? "and ROW_NUM BETWEEN "+((startPage-1)*onePage+1)+" AND "+((startPage+5)*onePage)+" " : " " )
					+ "order by ROW_NUM "
					+ "";

			rs = null;
			
			logger.info("Query user and belong: "+sql);
			rs = st.executeQuery(sql);
				
			while (rs.next()) {
				LogBean l = new LogBean();
				l.setAccount(rs.getString("ACCOUNT"));
				l.setAction(rs.getString("ACTION"));
				
				String params = rs.getString("PARAMS");
				if(params!=null) {
					if(params.startsWith("{")) {
						JSONObject j = new JSONObject(params);
						j.remove("SUBSIDIARYIDS");
						j.remove("subsidiaryids");
						j.remove("SUBSIDIARYID");
						j.remove("subsidiaryid");
						
						if(j.length()!=0)
							l.setParams(j.toString());
					}else {
						l.setParams(params);
					}
					
				}
				
				
				l.setTime(rs.getString("TIME"));
				
				result.add(l);
			} 
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}
	
	public int queryLogCount(Map<String, Object> condition) throws SQLException, ClassNotFoundException, ParseException {
		int result = 0;
		
		String account = (String) condition.get("account");
		
		String sql = "Select user_level from MTM_USER WHERE ACCOUNT = '"+account+"' ";
		int level = 10;
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
			
			String conditionString = processCondition(condition);
			
			if(level>0) {
				conditionString += "AND A.ACCOUNT in ( "
						+ "SELECT '"+account+"' from dual "
						+ "union "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"' "
						+ "union "
						+ "SELECT ACCOUNT FROM MTM_USER WHERE OWNER in (SELECT ACCOUNT FROM MTM_USER WHERE OWNER = '"+account+"') "
						+ ") ";
			}
			
			sql = ""
					+ "select count(1) CD "  
					+ "from MTM_ACTION_LOG A,MTM_USER B "  
					+ "where A.ACCOUNT = B.ACCOUNT "  
					+ conditionString
					+ "";
			
			rs = null;

			logger.info("Execute SQL:"+sql);
			rs = st.executeQuery(sql);
				
			if (rs.next()) {
				result = rs.getInt("CD");
			} 
		} finally {
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}

}
