package common.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import common.bean.LogBean;
import common.dao.LogDao;

@Service
public class LogService {

	@Resource
	LogDao logDao;

	public Map<String,String>  login(String acc,String pass) throws SQLException, ClassNotFoundException{
		return logDao.login(acc, pass);
	}
	
	public List<LogBean> queryLogDatas(int startPage, int onePage, String orderby, Boolean asc, Map<String, Object> condition) throws SQLException, ClassNotFoundException, ParseException {
		return logDao.queryLogDatas(startPage, onePage, orderby, asc,condition);
	}
	
	public int queryLogCount(Map<String, Object> condition ) throws SQLException, ClassNotFoundException, ParseException {
		return logDao.queryLogCount(condition);
	}

	public LogDao getLogDao() {
		return logDao;
	}

	public void setLogDao(LogDao logDao) {
		this.logDao = logDao;
	}
}
