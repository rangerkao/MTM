package common.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import common.dao.LoginDao;

@Service
public class LoginService {
	
	@Resource
	LoginDao loginDao;

	public Map<String,String>  login(String acc,String pass) throws SQLException, ClassNotFoundException{
		return loginDao.login(acc, pass);
	}

	public LoginDao getLoginDao() {
		return loginDao;
	}

	public void setLoginDao(LoginDao loginDao) {
		this.loginDao = loginDao;
	}
	
	
}
