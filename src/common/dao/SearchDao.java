package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import common.bean.DataBean;

@Repository
public class SearchDao extends BaseDao{
	
	
	public String processCondition(Map<String,Object> condition) throws ParseException {
		
		String sCondition=" ";
		if(condition.size()>0) {
			for(String key:condition.keySet()) {
				if("status".equalsIgnoreCase(key)) {
					sCondition += "AND B.status in ("+condition.get(key)+") ";//SERVICE B
				}else if("ICCID".equalsIgnoreCase(key)) {
					sCondition += "AND A.ICCID like '%"+condition.get(key)+"%' ";//SERVICE B
				}else if("IMSI".equalsIgnoreCase(key)) {
					sCondition += "AND A.IMSI like '%"+condition.get(key)+"%' ";//IMSI A
				}else if("start".equalsIgnoreCase(key)) {
					sCondition += "AND C.DAY>= '"+condition.get(key).toString().replaceAll("/", "")+"' ";//START C
				}else if("end".equalsIgnoreCase(key)) {
					DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					Date endDate = sdf.parse((String)condition.get(key));
					sCondition += "AND C.DAY < '"+sdf.format(endDate.getTime()+24*60*60*1000).replaceAll("/", "")+"' ";//END C
				}
			}
		}
		
		return sCondition;
	}
	
	public List<DataBean> queryDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		List<DataBean> result = new ArrayList<DataBean>();
		
		if(orderby ==null || "".equals(orderby)) {
			orderby = "SERVICEID";
			asc = false;
		}else if("lastVisitedNetwork".equalsIgnoreCase(orderby)) {
			orderby = "LVN";
		}
		
		String sql = "select SERVICEID,ICCID,IMSI,STATUS,MMENAME,IMEI,SGSNNUMBER,PIN1,PIN2,PUK1,PUK2 "
				+ "from ("
				+ " select SERVICEID,ICCID,IMSI,STATUS,MMENAME,IMEI,SGSNNUMBER,PIN1,PIN2,PUK1,PUK2 "
				+ "			,ROW_NUMBER() OVER(ORDER BY "+orderby+" "+(asc?"ASC":"DESC")+") ROW_NUM "
				+ " from ("
				+ "  select B.SERVICEID,A.ICCID,A.IMSI,  "
				+ "	 CASE B.STATUS  "
				+ "     when '0' then 'Activation Ready' "
				+ "		when '1' then 'Activated'  "
				+ "		when '3' then 'De-activated'  "
				+ "		when '4' then 'Retired'  "
				+ "		when '10' then 'Retired'  "
				+ "		else 'Unknown'  "
				+ "	end STATUS,  "
				+ " C.MMENAME MMENAME, "
				+ " C.IMEI IMEI, "
				+ " D.SGSNNUMBER SGSNNUMBER,"
				+ " A.PIN1,A.PIN2,A.PUK1,A.PUK2 "
				+ "	from IMSI A,SERVICE B,EPSPROFILE C,BASICPROFILE D,"
				+ "			( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
				+ "				from ( "
				+ "						select B.SERVICEID,A.FIELDVALUE "
				+ "						FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101') A, "
				+ "					( "
				+ "						select B.SERVICEID,A.newvalue "
				+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101' ) B "
				+ "				where A.serviceid = B.serviceid(+)) E "
				+ "WHERE A.IMSI = E.IMSI AND B.SERVICEID = E.SERVICEID  "
				+ " AND B.SERVICEID = C.SERVICEID(+) AND B.SERVICEID = D.SERVICEID(+) "
				+ (subsidiaryids != null && !"".equals(subsidiaryids.trim())? " and B.subsidiaryid in ("+subsidiaryids+") " : " ")
				+ processCondition(condition)
				+ " )) "
				+ "where 1=1 "
				+ (onePage >=0 ? "and ROW_NUM BETWEEN "+((startPage-1)*onePage+1)+" AND "+((startPage+5)*onePage)+" " : " " )
				+ "order by ROW_NUM ";
		
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			
			logger.info("query datas:"+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				DataBean d = new DataBean();
				d.setServiceid(rs.getLong("SERVICEID"));
				d.setIccid(rs.getString("ICCID"));
				d.setImsi(rs.getString("IMSI"));
				d.setStatus(rs.getString("STATUS"));;
				/*d.setLastVisitedNetwork(rs.getString("LVN"));*/
				d.setMmename(rs.getString("MMENAME"));
				d.setImei(rs.getString("IMEI"));
				d.setSgsnnumber(rs.getString("SGSNNUMBER"));
				d.setPin1(rs.getString("PIN1"));
				d.setPin2(rs.getString("PIN2"));
				d.setPuk1(rs.getString("PUK1"));
				d.setPuk2(rs.getString("PUK2"));
				result.add(d);
			}
		} finally{
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}
	
	public int queryCount(String subsidiaryids,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		int result = 0;
		
		String sql = 
				"select count(1) CD "
				+ "	from IMSI A,SERVICE B,EPSPROFILE C,BASICPROFILE D,  "
				+ "			( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
				+ "				from ( "
				+ "						select B.SERVICEID,A.FIELDVALUE "
				+ "						FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101') A, "
				+ "					( "
				+ "						select B.SERVICEID,A.newvalue "
				+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101' ) B "
				+ "				where A.serviceid = B.serviceid(+)) E "
				+ "WHERE A.IMSI = E.IMSI AND B.SERVICEID = E.SERVICEID  "
				+ (subsidiaryids != null && !"".equals(subsidiaryids.trim())? " and B.subsidiaryid in ("+subsidiaryids+") " : " ")
				+ " AND B.SERVICEID = C.SERVICEID(+) AND B.SERVICEID = D.SERVICEID(+) "
				//+ " AND C.VISITEDPLMNID is not null and C.VISITEDPLMNID <> 'MCC-MNC' "
				//+ " AND D.SGSNNUMBER is not null "
				+ processCondition(condition);
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			logger.info("query count:"+sql);
			rs = st.executeQuery(sql);
			
			if(rs.next()) {
				result = rs.getInt("CD");
			}
		} finally{
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}
	
	public List<DataBean> queryUsageDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition) throws Exception {
		List<DataBean> result = new ArrayList<DataBean>();
		
		if(orderby ==null || "".equals(orderby)) {
			orderby = "DAY";
			asc = false;
		}else if("lastUsageTime".equalsIgnoreCase(orderby)) {
			orderby = "LAST_DATA_TIME";
		}else if("network".equalsIgnoreCase(orderby)) {
			orderby = "MCCMNC";
		}else if("amount".equalsIgnoreCase(orderby)) {
			orderby = "CHARGE";
		}
		
		String sql = " select IMSI,ICCID,serviceid,subsidiaryid,MCCMNC,DAY,VOLUME,LAST_DATA_TIME,CHARGE "
				+ " from ("
				+ " select IMSI,ICCID,serviceid,subsidiaryid,MCCMNC,DAY,VOLUME,LAST_DATA_TIME,CHARGE "
				+ ",ROW_NUMBER() OVER(ORDER BY "+orderby+" "+(asc?"ASC":"DESC")+") ROW_NUM "
				+ " from ("
				+ "  select A.IMSI,A.ICCID,B.serviceid,B.subsidiaryid,C.MCCMNC,C.DAY,C.VOLUME,C.LAST_DATA_TIME,C.CHARGE "
				//+ "	 ROW_NUMBER() OVER(ORDER BY "+orderby+" "+(asc?"ASC":"DESC")+") ROW_NUM  "
				+ "  from imsi A,service B,HUR_CURRENT_DAY C, " 
				+ "			( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
				+ "				from ( "
				+ "						select B.SERVICEID,A.FIELDVALUE "
				+ "						FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101') A, "
				+ "					( "
				+ "						select B.SERVICEID,A.newvalue "
				+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101' ) B "
				+ "				where A.serviceid = B.serviceid(+)) E "
				+ "WHERE A.IMSI = E.IMSI AND B.SERVICEID = E.SERVICEID  "
				+ "AND B.SERVICEID = C.SERVICEID"
				+ (subsidiaryids != null && !"".equals(subsidiaryids.trim())? " and B.subsidiaryid in ("+subsidiaryids+") " : " ")
				+ processCondition(condition)
				+ " )) where 1=1 "
				+ (onePage >=0 ? "and ROW_NUM BETWEEN "+((startPage-1)*onePage+1)+" AND "+((startPage+5)*onePage)+" " : " " )
				+ "order by ROW_NUM ";
		
		
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			
			conn = getConn1();
			st = conn.createStatement();
			
			logger.info("query usage datas:"+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				DataBean d = new DataBean();
				d.setServiceid(rs.getLong("SERVICEID"));
				d.setIccid(rs.getString("ICCID"));
				d.setImsi(rs.getString("IMSI"));
				d.setNetwork(rs.getString("MCCMNC"));
				String day = rs.getString("DAY");
				d.setDay(day.substring(0, 4)+"/"+day.substring(4, 6)+"/"+day.substring(6, 8));
				d.setVolume(rs.getString("VOLUME")!=null?FormatDouble(rs.getDouble("VOLUME"),"#,##0"):" ");
				d.setLastUsageTime(rs.getString("LAST_DATA_TIME")!=null?rs.getString("LAST_DATA_TIME").replace(".0", ""):" ");
				d.setAmount(FormatDouble(rs.getDouble("CHARGE"),"#,##0.0000"));
				result.add(d);
			}
		} finally{
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}
	
	public int queryUsageCount(String subsidiaryids,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		int result = 0;
		
		String sql = " "
				+ " select count(1) CD  "
				+ " from imsi A,service B,HUR_CURRENT_DAY C, " 
				+ "			( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
				+ "				from ( "
				+ "						select B.SERVICEID,A.FIELDVALUE "
				+ "						FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101') A, "
				+ "					( "
				+ "						select B.SERVICEID,A.newvalue "
				+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101' ) B "
				+ "				where A.serviceid = B.serviceid(+)) E "
				+ "WHERE A.IMSI = E.IMSI AND B.SERVICEID = E.SERVICEID  "
				+ "AND B.SERVICEID = C.SERVICEID"
				+ (subsidiaryids != null && !"".equals(subsidiaryids.trim())? " and B.subsidiaryid in ("+subsidiaryids+") " : " ")
				+ processCondition(condition);
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			logger.info("query count:"+sql);
			rs = st.executeQuery(sql);
			
			if(rs.next()) {
				result = rs.getInt("CD");
			}
		} finally{
			closeConnection(conn, st, ps, rs);
		}
		return result;
	}
	
	public List<DataBean> queryAllDatas(String subsidiaryids,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		List<DataBean> result = new ArrayList<DataBean>();
		

		String sql = "select SERVICEID,ICCID,IMSI,STATUS,MMENAME,IMEI,SGSNNUMBER,PIN1,PIN2,PUK1,PUK2 "
				+ "from ("
				+ " select SERVICEID,ICCID,IMSI,STATUS,MMENAME,IMEI,SGSNNUMBER,PIN1,PIN2,PUK1,PUK2 "
				+ " from ("
				+ "  select B.SERVICEID,A.ICCID,A.IMSI,  "
				+ "	 CASE B.STATUS  "
				+ "     when '0' then 'Activation Ready' "
				+ "		when '1' then 'Activated'  "
				+ "		when '3' then 'De-activated'  "
				+ "		when '4' then 'Retired'  "
				+ "		when '10' then 'Retired'  "
				+ "		else 'Unknown'  "
				+ "	end STATUS,  "
				//+ "	ROW_NUMBER() OVER(ORDER BY "+orderby+" "+(asc?"ASC":"DESC")+") ROW_NUM,  "
				/*+ " case when VISITEDPLMNID is not null and VISITEDPLMNID <> 'MCC-MNC' then VISITEDPLMNID "
				+ " else case when SGSNNUMBER is not null then SGSNNUMBER else '' end end LVN "*/
				+ " C.MMENAME MMENAME, "
				+ " C.IMEI IMEI, "
				+ " D.SGSNNUMBER SGSNNUMBER,"
				+ " A.PIN1,A.PIN2,A.PUK1,A.PUK2 "
				+ "	from IMSI A,SERVICE B,EPSPROFILE C,BASICPROFILE D,  "
				+ "			( 	select A.serviceid,nvl(B.newvalue,A.FIELDVALUE) IMSI "
				+ "				from ( "
				+ "						select B.SERVICEID,A.FIELDVALUE "
				+ "						FROM NEWSERVICEORDERINFO A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101') A, "
				+ "					( "
				+ "						select B.SERVICEID,A.newvalue "
				+ "						FROM SERVICEINFOCHANGEORDER A, SERVICEORDER B "
				+ "						where A.FIELDID=3713 AND A.ORDERID=B.ORDERID "
				+ "						AND TO_CHAR(a.completedate,'yyyymmdd')>='20180101' ) B "
				+ "				where A.serviceid = B.serviceid(+)) E "
				+ "WHERE A.IMSI = E.IMSI AND B.SERVICEID = E.SERVICEID  "
				//+ "	WHERE A.SERVICEID = B.SERVICEID "
				+ (subsidiaryids != null && !"".equals(subsidiaryids.trim())? " and B.subsidiaryid in ("+subsidiaryids+") " : " ")
				+ " AND B.SERVICEID = C.SERVICEID(+) AND B.SERVICEID = D.SERVICEID(+) "
				//+ " AND C.VISITEDPLMNID is not null and C.VISITEDPLMNID <> 'MCC-MNC' "
				//+ " AND D.SGSNNUMBER is not null "
				+ processCondition(condition)
				+ " )) ";		
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConn1();
			st = conn.createStatement();
			
			logger.info("query datas:"+sql);
			rs = st.executeQuery(sql);
			
			while(rs.next()) {
				DataBean d = new DataBean();
				d.setServiceid(rs.getLong("SERVICEID"));
				d.setIccid(rs.getString("ICCID"));
				d.setImsi(rs.getString("IMSI"));
				d.setStatus(rs.getString("STATUS"));;
				/*d.setLastVisitedNetwork(rs.getString("LVN"));*/
				d.setMmename(rs.getString("MMENAME"));
				d.setImei(rs.getString("IMEI"));
				d.setSgsnnumber(rs.getString("SGSNNUMBER"));
				d.setPin1(rs.getString("PIN1"));
				d.setPin2(rs.getString("PIN2"));
				d.setPuk1(rs.getString("PUK1"));
				d.setPuk2(rs.getString("PUK2"));
				result.add(d);
			}
		} finally{
			closeConnection(conn,st,ps,rs);
		}
		return result;
	}
	
	public String FormatDouble(Double value,String form) throws Exception{
		
		if(value == null)
			throw new Exception("Input could't be null.");
		
		if(form==null || "".equals(form)){
			form="0.00";
		}
		
		return new DecimalFormat(form).format(value);
	}

}
