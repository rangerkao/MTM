package common.cache;
 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Controller;

import common.action.BaseAction;

public class CacheAction extends BaseAction{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Properties props =new Properties();
	
	static Long ConnectionTime1;
	static Long ConnectionTime2;
	static Long ConnectionTime3;
	
	static Connection conn1;
	static Connection conn2;
	static Connection conn3;
	
	static SimpleConnectionPoolDataSource CPD1 ;
	static SimpleConnectionPoolDataSource CPD2 ;
	static SimpleConnectionPoolDataSource CPD3 ;
	
	static boolean threadExit= false;
	static long connectionCloseTime = 1000*60*20;
	public static Logger logger = null;
	public static int MAX_CONNECTIONS = 50;
	static{
		System.out.println("loadPropertiesing...!");
		try {
			loadProperties(CacheAction.class.getClassLoader().getResource("").toString().replace("file:", "").replace("%20", " "));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			CPD1 = new SimpleConnectionPoolDataSource(
					props.getProperty("Oracle.DriverClass"), 
					props.getProperty("Oracle.URL")
					.replace("{{Host}}", props.getProperty("Oracle.Host"))
					.replace("{{Port}}", props.getProperty("Oracle.Port"))
					.replace("{{ServiceName}}", 	(props.getProperty("Oracle.ServiceName")!=null?props.getProperty("Oracle.ServiceName"):""))
					.replace("{{SID}}", (props.getProperty("Oracle.SID")!=null?props.getProperty("Oracle.SID"):"")), 
					props.getProperty("Oracle.UserName"), 
					props.getProperty("Oracle.PassWord"),
					MAX_CONNECTIONS,logger);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			CPD2 = new SimpleConnectionPoolDataSource(
					props.getProperty("mBOSS.DriverClass"), 
					props.getProperty("mBOSS.URL")
					.replace("{{Host}}", props.getProperty("mBOSS.Host"))
					.replace("{{Port}}", props.getProperty("mBOSS.Port"))
					.replace("{{ServiceName}}", (props.getProperty("mBOSS.ServiceName")!=null?props.getProperty("mBOSS.ServiceName"):""))
					.replace("{{SID}}", (props.getProperty("mBOSS.SID")!=null?props.getProperty("mBOSS.SID"):"")), 
					props.getProperty("mBOSS.UserName"), 
					props.getProperty("mBOSS.PassWord"),
					MAX_CONNECTIONS,logger);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			CPD3 = new SimpleConnectionPoolDataSource(
					props.getProperty("CRMDB.DriverClass"), 
					props.getProperty("CRMDB.URL")
					.replace("{{Host}}", props.getProperty("CRMDB.Host"))
					.replace("{{Port}}", props.getProperty("CRMDB.Port"))
					.replace("{{DBName}}", props.getProperty("CRMDB.DBName")), 
					props.getProperty("CRMDB.UserName"), props.getProperty("CRMDB.PassWord"),
					MAX_CONNECTIONS,logger);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Thread t = new Thread(new ConnectionWatch());
		t.start();
			
	}
	
	public static class ConnectionWatch implements Runnable{
		long nowTime;

		@Override
		public void run() {
			while(!threadExit){
				logger.info("ConnectionWatch...");
				try {
					Thread.sleep(connectionCloseTime);
				} catch (InterruptedException e) {}
				
				nowTime = System.currentTimeMillis();
		
				List<Connection> conns1 = null;
				
				try {
					logger.info("Watch 1");
					conns1 = CPD1.getConns();
					synchronized (conns1) {
						Iterator<Connection> it1 = conns1.iterator();
						while (it1.hasNext()) {
							ConnectionWrapper conn = (ConnectionWrapper) it1.next();
							
							if (nowTime-conn.getExecuteTime()>connectionCloseTime-1000*60*5 || !connTest(conn)){
								it1.remove();
								try {
									conn.doClose();
									logger.info("Watch 1 after release!"+CPD1.getConns().size()+"||"+conns1.size());
								} catch (SQLException e) {}
							}
						}
					}
					logger.info("Watch 1 after watches!"+CPD1.getConns().size());
				} catch (Exception e1) {
					logger.info(e1);
				}		
			
				try {
					logger.info("Watch 2");
					List<Connection> conns2 = CPD2.getConns();
					synchronized (conns2) {
						Iterator<Connection> it2 = conns2.iterator();
						while (it2.hasNext()) {
							ConnectionWrapper conn = (ConnectionWrapper) it2.next();
							if (nowTime-conn.getExecuteTime()>connectionCloseTime-1000*60*5|| !connTest(conn)){
								it2.remove();
								try {
									conn.doClose();
									logger.info("Watch 2 after release!"+CPD2.getConns().size()+"||"+conns2.size());
								} catch (SQLException e) {}
							}
						}
					}
					logger.info("Watch 2 after watches!"+CPD2.getConns().size());
				} catch (Exception e1) {
					logger.info(e1);
				}		
				
				try {
					logger.info("Watch 3");
					List<Connection> conns3 = CPD3.getConns();
					synchronized (conns3) {
						Iterator<Connection> it3 = conns3.iterator();
						while (it3.hasNext()) {
							ConnectionWrapper conn = (ConnectionWrapper) it3.next();
							if (nowTime-conn.getExecuteTime()>connectionCloseTime-1000*60*5 || !connTest(conn)){
								it3.remove();
								try {
									conn.doClose();
								} catch (SQLException e) {}
							}
						}
					}
					logger.info("Watch 3 after watches!"+CPD3.getConns().size());
				} catch (Exception e) {
					logger.info(e);
				}		
			}
		}		
	}
	
	
	//Load Properties
	static void loadProperties(String path) throws FileNotFoundException, IOException{
		props.load(new FileInputStream(path+ "/log4j.properties"));
		PropertyConfigurator.configure(props);
		logger = Logger.getLogger("MTM");
		//logger = Logger.getLogger("RFP");
		System.out.println("loadProperties success!");
	}
	
	
	public static Connection getConn1() throws ClassNotFoundException, SQLException{
		logger.info("get Connect1!");
		/*ConnectionTime1 = System.currentTimeMillis();
		try {
			if(conn1 == null)
				throw new Exception("null Connection");
			connTest(conn1);
		} catch (Exception e) {
			try {
				conn1.close();
			} catch (Exception e1) {}	
			conn1 = cerateConnection(
					props.getProperty("Oracle.DriverClass"), 
					props.getProperty("Oracle.URL")
					.replace("{{Host}}", props.getProperty("Oracle.Host"))
					.replace("{{Port}}", props.getProperty("Oracle.Port"))
					.replace("{{ServiceName}}", 	(props.getProperty("Oracle.ServiceName")!=null?props.getProperty("Oracle.ServiceName"):""))
					.replace("{{SID}}", (props.getProperty("Oracle.SID")!=null?props.getProperty("Oracle.SID"):"")), 
					props.getProperty("Oracle.UserName"), props.getProperty("Oracle.PassWord"));
		}
		return conn1;*/
		
		
		Connection con = CPD1.getConnection();
		logger.info("AutoCommit():"+con.getAutoCommit());
		return con;
	}

	public static void releaseConn1(Connection conn) throws SQLException{
		System.out.println("Connect1!");
//		if(conn==null) return;
//		cp.releaseConnection(conn);
		conn.close();
		logger.info("release 1!"+CPD1.getConns().size());
	}
	
	public static Connection getConn2() throws ClassNotFoundException, SQLException{
		logger.info("get Connect2!");
		/*ConnectionTime2 = System.currentTimeMillis();
		try {
			if(conn2 == null)
				throw new Exception("null Connection");
			connTest(conn2);
		} catch (Exception e) {
			try {
				conn2.close();
			} catch (Exception e1) {}	
			conn2 = cerateConnection(
					props.getProperty("mBOSS.DriverClass"), 
					props.getProperty("mBOSS.URL")
					.replace("{{Host}}", props.getProperty("mBOSS.Host"))
					.replace("{{Port}}", props.getProperty("mBOSS.Port"))
					.replace("{{ServiceName}}", (props.getProperty("mBOSS.ServiceName")!=null?props.getProperty("mBOSS.ServiceName"):""))
					.replace("{{SID}}", (props.getProperty("mBOSS.SID")!=null?props.getProperty("mBOSS.SID"):"")), 
					props.getProperty("mBOSS.UserName"), 
					props.getProperty("mBOSS.PassWord"));
		}
		return conn2;*/
		Connection con = CPD2.getConnection();
		logger.info("AutoCommit():"+con.getAutoCommit());
		return con;
	}
	
	public static void releaseConn2(Connection conn) throws SQLException{
//		if(conn==null) return;
//		cp.releaseConnection(conn);
		conn.close();
		logger.info("release 2!"+CPD2.getConns().size());
	}
	
public static Connection getConn3() throws ClassNotFoundException, SQLException{
	logger.info("get Connect3!");
	/*ConnectionTime3 = System.currentTimeMillis();
		try {
			if(conn3 == null)
				throw new Exception("null Connection");
			connTest(conn3);
		} catch (Exception e) {
			try {
				conn3.close();
			} catch (Exception e1) {}	
			conn3 = cerateConnection(
					props.getProperty("CRMDB.DriverClass"), 
					props.getProperty("CRMDB.URL")
					.replace("{{Host}}", props.getProperty("CRMDB.Host"))
					.replace("{{Port}}", props.getProperty("CRMDB.Port"))
					.replace("{{DBName}}", props.getProperty("CRMDB.DBName")), 
					props.getProperty("CRMDB.UserName"), props.getProperty("CRMDB.PassWord"));
		}
		return conn3;*/
	Connection con = CPD3.getConnection();
	logger.info("AutoCommit():"+con.getAutoCommit());
	return con;
	}

	
	public static void releaseConn3(Connection conn) throws SQLException{
		
//		if(conn==null) return;
//		cp.releaseConnection(conn);
		conn.close();
		logger.info("release 3!"+CPD3.getConns().size());
		
	}
	
	
	private static boolean connTest(Connection conn){
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeQuery("select 'OK' from dual");
		} catch (SQLException e) {
			return false;
		}finally{
			try {
			if(st!=null)	st.close();
			} catch (SQLException e) {	};
		}
		return true;
	}
		
	
	private static Connection cerateConnection(String driver,String url,String user,String pass) throws ClassNotFoundException, SQLException{
		 System.out.println("create connction."+url);
		 Class.forName(driver);
		 return DriverManager.getConnection(url, user, pass); 
	 }
			
	
	/**
	 * Error mail
	 * 
	 * @param content
	 */
	public static void sendMail(String Receiver,String Subject,String Content){
	
		try {
			sendMail(Subject, Content, "DVRS UI", Receiver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	static void sendMail(String mailSubject,String mailContent,String mailSender,String mailReceiver){
		String [] cmd=new String[3];
		cmd[0]="/bin/bash";
		cmd[1]="-c";
		cmd[2]= "/bin/echo \""+mailContent+"\" | /bin/mail -s \""+mailSubject+"\" -r "+mailSender+" "+mailReceiver;

		try{
			Process p = Runtime.getRuntime().exec (cmd);
			p.waitFor();
			System.out.println("send mail cmd:"+cmd);
		}catch (Exception e){
			System.out.println("send mail fail:"+mailContent);
		}
	}
	
	public static void actionLog(String account,String action,String params) throws ClassNotFoundException, SQLException{
		
		String sql = "insert into MTM_ACTION_LOG(ACCOUNT,ACTION,PARAMS) VALUES('"+account+"','"+action+"','"+(params!=null?params:"")+"')";
		
		Connection conn1 = null;
		Statement st = null;
		try {
			conn1 = getConn1();
			st = conn1.createStatement();
			logger.info("Execute SQL:" + sql);
			st.executeUpdate(sql);
		} finally {
			conn1.close();
			st.close();
		}
		
		
	}
	
}
