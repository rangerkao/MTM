package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ProvisionDao extends BaseDao{
	
	Connection conn = null;
	Statement st = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	
	public void provision(List<Object> imsis,String request) throws Exception {
		
		
		try {
			conn = getConn1();
			//conn1.setAutoCommit(false);
			st = conn.createStatement();
			//reset
			if ("00".equals(request)) {
				String sql = "";
				for (Object imsi : imsis) {

					String s2tIMSI = (String) imsi;
					String s2tMsisdn = getMsisdn((String) imsi);

					sql = "insert into HUR_QOSRESET_LOG(IMSI,MSISDN,MCCMNC,TYPE)" + "VALUES('" + imsi + "','"
							+ s2tMsisdn + "','MTM','MTM')";
					logger.debug("insertQosResetLog SQL : " + sql);
					st.execute(sql);
					logger.info("commit");
					conn.commit();
				}
			} else {

				Date now = new Date();
				//請求日期 "yyyyMMdd"
				SimpleDateFormat reQueSdf = new SimpleDateFormat("yyyyMMdd");
				//請求時間 "MM/dd/yyyy HH:mm:ss"
				SimpleDateFormat sDateSdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

				for (Object imsi : imsis) {

					String s2tIMSI = (String) imsi;
					String s2tMsisdn = getMsisdn((String) imsi);

					String reqDate = reQueSdf.format(now);
					String sdate = sDateSdf.format(now);

					String MnoCode = "984";
					//Get Count 
					String sCount = getCount();

					//getFileId
					String fileId = getFileId();

					//get workOrderNBR
					String workOrderNBR = getWorkOrderNBR();

					//get Service order NBR
					String cServiceOrderNBR = getServiceOrderNBR();

					String c910SEQ = reqDate + sCount;
					String fileName = "S2TCI" + c910SEQ + "." + MnoCode;
					String ticketNumber = "MTM" + reqDate + sCount;

					doSyncFile(fileId, fileName, reqDate, sCount, MnoCode);
					doSyncFileDTL(fileId, c910SEQ, sdate, ticketNumber, cServiceOrderNBR, workOrderNBR, s2tIMSI,
							s2tMsisdn, request);

					String subCode = null;
					String[][] dtls;

					if ("01".equals(request)) {
						subCode = "004";
						dtls = new String[][] { { cServiceOrderNBR, "1", "2", "1", s2tMsisdn },
								{ cServiceOrderNBR, "1", "37", "0", "999999998" },
								{ cServiceOrderNBR, "1", "72", "0", "0" },
								{ cServiceOrderNBR, "1", "122", "1", "0" }, };
					} else if ("02".equals(request)) {
						subCode = "005";
						dtls = new String[][] { { cServiceOrderNBR, "1", "2", "1", s2tMsisdn },
								{ cServiceOrderNBR, "1", "37", "0", "999999998" },
								{ cServiceOrderNBR, "1", "122", "1", "0" }, };
					} else if ("16".equals(request)) {
						subCode = "056";
						String M_GPRS = null;
						String N_GPRS = null;
						dtls = new String[][] { { cServiceOrderNBR, "1", "2", "1", s2tMsisdn },
								{ cServiceOrderNBR, "1", "1945", "0", M_GPRS },
								{ cServiceOrderNBR, "1", "1946", "1", N_GPRS }, };

					} else if ("99".equals(request)) {
						subCode = "006";
						dtls = new String[][] { { cServiceOrderNBR, "1", "2", "1", s2tMsisdn },
								{ cServiceOrderNBR, "1", "37", "0", "999999998" },
								{ cServiceOrderNBR, "1", "188", "0", "0" }, };
					} else {
						break;
					}
					doServiceOrderItem(cServiceOrderNBR, subCode);
					doServiceOrderItemDTL(dtls);
					doServiceOrder(cServiceOrderNBR, workOrderNBR, s2tIMSI, s2tMsisdn, request);
					
					conn.commit();
					
					String sql = ""
							+ "select STATUS "
							+ "from S2T_TB_SERVICE_ORDER  "
							+ "where SERVICE_ORDER_NBR = '"+cServiceOrderNBR+"' "
							+ "";
					String status = null;
					
					for(int i = 0 ; i < 10 ;  i++ ) {
						rs = null;
						rs = st.executeQuery(sql);
						
						if(rs.next()) {
							status = rs.getString("STATUS");
						}
						if("Y".equals(status)) {
							break;
						}else if("F".equals(status)) {
							sql = ""
									+ "select ERROR_CDE,ERROR_DESC "
									+ "from S2T_TB_SERVICE_ORDER_ITEM "
									+ "where SERVICE_ORDER_NBR = '"+cServiceOrderNBR+"' "
									+ "";
							rs = null;
							rs = st.executeQuery(sql);
							if(rs.next()) {
								throw new Exception("At provision "+imsi+"/"+s2tMsisdn+"("+cServiceOrderNBR+") got error "+rs.getString("")+"("+new String(rs.getString("").getBytes("ISO-88591"),"big5")+").");
							}else {
								throw new Exception("Unknow error.");
							}
						}
						
						Thread.sleep(3000);
					}
					
					if("N".equals(status)) {
						throw new Exception("Isap is too slow or unready.");
					}
				}
				
			} 
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.info("rollback");
			conn.rollback();
			throw e;
		}finally {
			closeConnection(conn, st, ps, rs);
		}
		
	}
	
	public void doSyncFile(String fileId,String fileName,String reqDateSub,String c910SEQSub,String MNOSubCode) throws SQLException{
		logger.info("Insert syncFile...");
    	//INSERT INTO S2T_TB_TYPEB_WO_SYNC_FILE (FILE_ID,FILE_NAME,FILE_SEND_DATE,FILE_SEQ,CMCC_BRANCH_ID,FILE_CREATE_DATE,STATUS)   	 VALUES ([cFileID],'[cFileName]','[dReqDate.substring(0, 8)]','[c910SEQ.substring(8, 11) ]','950',sysdate,'[sSFStatus]');
    	String sql = "INSERT INTO S2T_TB_TYPEB_WO_SYNC_FILE (FILE_ID,FILE_NAME,FILE_SEND_DATE,FILE_SEQ,CMCC_BRANCH_ID,FILE_CREATE_DATE,STATUS)  "
    			+ "									VALUES ("+fileId+",'"+fileName+"','"+reqDateSub+"','"+c910SEQSub+"','"+MNOSubCode+"',sysdate,'V')";
    	logger.info("Execut SQL:"+sql);
    	st.executeUpdate(sql);
	}
	
	public void doSyncFileDTL(String fileId,String c910SEQ,String sdate, String ticketNumber,String cServiceOrderNBR,String workOrderNBR,String s2tIMSI,String s2tMsisdn,String request) throws SQLException{
		logger.info("Insert syncFileDTL...");
		String sql = "INSERT INTO S2T_TB_TYPB_WO_SYNC_FILE_DTL (WORK_ORDER_NBR,WORK_TYPE, FILE_ID, SEQ_NO, CMCC_OPERATIONDATE, ORIGINAL_CMCC_IMSI,ORIGINAL_CMCC_MSISDN, S2T_IMSI, S2T_MSISDN, FORWARD_TO_HOME_NO,FORWARD_TO_S2T_NO_1, IMSI_FLAG, STATUS, SERVICE_ORDER_NBR, SUBSCR_ID,S2T_OPERATIONDATE) "
                + "VALUES ("+workOrderNBR+",'"+request+"',"+fileId+",'"+c910SEQ+"',to_date('"+sdate+"','MM/dd/yyyy hh24:mi:ss'),'"+s2tIMSI+"','"+s2tMsisdn+"','"+s2tIMSI+"','"+s2tMsisdn+"','"+s2tMsisdn+"','"+s2tMsisdn+"', '1', 'V','"+cServiceOrderNBR+"','"+ticketNumber+"',sysdate)";
		logger.info("Execut SQL:"+sql);
		st.executeUpdate(sql);
	}
	public void doServiceOrder(String cServiceOrderNBR,String workOrderNBR,String s2tIMSI,String s2tMsisdn,String request) throws SQLException{
		logger.info("Insert serviceOrder...");
    	//INSERT INTO S2T_TB_SERVICE_ORDER (SERVICE_ORDER_NBR,WORK_TYPE, S2T_MSISDN, SOURCE_TYPE, SOURCE_ID, STATUS, CREATE_DATE) VALUES ('[cServiceOrderNBR]','[cReqStatus ]','[cS2TMSISDN]','B_TYPE',[cWorkOrderNBR ], '', sysdate);
    	String sql = "INSERT INTO S2T_TB_SERVICE_ORDER (SERVICE_ORDER_NBR,WORK_TYPE, S2T_MSISDN, SOURCE_TYPE, SOURCE_ID, STATUS, CREATE_DATE) "
    			+ "								VALUES ('"+cServiceOrderNBR+"','"+request+"','"+s2tMsisdn+"','B_TYPE',"+workOrderNBR+", 'N', sysdate)";
    	logger.info("Execut SQL:"+sql);
    	st.executeUpdate(sql);
	}
	public void doServiceOrderItem(String cServiceOrderNBR,String subCode) throws SQLException{
		logger.info("Insert ServiceOrderItem...");
    	String sql = "Insert into S2T_TB_SERVICE_ORDER_ITEM (SERVICE_ORDER_NBR,STEP_NO, SUB_CODE, IDENTIFIER, STATUS, SEND_DATE) "
    			+ "									Values ("+cServiceOrderNBR+",1,'"+subCode+"',S2T_SQ_SERVICE_ORDER_ITEM.nextval, 'N', sysdate)";
    	logger.info("Execut SQL:"+sql);
    	st.executeUpdate(sql);
	}
	public void doServiceOrderItemDTL(String[][] dtls) throws SQLException{
		logger.info("Insert ServiceOrderItemDtl...");
		String sql;
		for(int i = 0 ; i<dtls.length;i++){
			sql = "Insert into S2T_TB_SERVICE_ORDER_ITEM_DTL (SERVICE_ORDER_NBR, STEP_NO, TYPE_CODE, DATA_TYPE, VALUE) "
					+ "VALUES ("+dtls[i][0]+","+dtls[i][1]+","+dtls[i][2]+","+dtls[i][3]+",'"+dtls[i][4]+"')";
			logger.info("Execut SQL:"+sql);
	    	st.executeUpdate(sql);
		}
	}
	
	public String getMsisdn(String imsi) throws SQLException {
		String sql = "select a.servicecode from service a,imsi b where a.serviceid = b.serviceid and b.imsi = '"+imsi+"' ";
	
		logger.info("get Msisdn : "+sql);
		
		rs = st.executeQuery(sql);
		
		if(rs.next()) {
			return rs.getString("servicecode");
		}
		
		return null;
	}
	
	public String getCount() throws SQLException {
		// 找出最後序號
		String sql = ""
				+ "select MAX(A.FILE_SEQ) +1 cd "
				+ "from S2T_TB_TYPEB_WO_SYNC_FILE A "
				+ "where length(A.FILE_SEQ) = 4 "
				+ "AND to_char(A.FILE_CREATE_DATE,'yyyyMMdd') = to_char(sysdate,'yyyyMMdd')";
		
		logger.info("sql");
		rs = st.executeQuery(sql);
		
		int c = 1;
		while (rs.next()) {
			c = rs.getInt("cd");
		}
		
		String cs = String.valueOf(c);
		
		for(int i = cs.length() ; i <4 ; i++) {
			cs = "0"+cs;
		}
		
		return cs;
	}
	
	public String getFileId() throws SQLException {
		String sql = null;
		//get File ID
		logger.info("get file Id...");
		String fileId = "";    
		rs = null;
		logger.info("Execut SQL:"+sql);
		rs = st.executeQuery("select S2T_SQ_FILE_CNTRL.NEXTVAL as ab from dual");
		while(rs.next()){
			fileId = rs.getString("ab");
		}
		return fileId;
	}
	public String getWorkOrderNBR() throws SQLException {
		String sql = null;
		//get work order NBR
		logger.info("get workOrderNBR...");
		String workOrderNBR = "";
		rs = null;
		logger.info("Execut SQL:"+sql);
		rs = st.executeQuery("select S2T_SQ_WORK_ORDER.nextval as ab from dual");
		while(rs.next()){
			workOrderNBR = rs.getString("ab");
		}
		return workOrderNBR;
	}
	public String getServiceOrderNBR() throws SQLException {
		String sql = null;
		logger.info("get cServiceOrderNBR...");
		String cServiceOrderNBR = "";
		rs =null;
		logger.info("Execut SQL:"+sql);
		rs = st.executeQuery("select S2T_SQ_SERVICE_ORDER.nextval as ab from dual");
		while(rs.next()){
			cServiceOrderNBR = rs.getString("ab");
		}
		return cServiceOrderNBR;
	}

}
