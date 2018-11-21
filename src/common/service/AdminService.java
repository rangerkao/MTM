package common.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import common.bean.PermissionBean;
import common.dao.AdminDao;

@Service
public class AdminService {

	@Resource
	AdminDao adminDao;
	
	public List<PermissionBean> queryPermission(String account) throws ClassNotFoundException, SQLException{
		return adminDao.queryPermission(account);
	}
	
	public Map<String,String> queryUserList(int level) throws SQLException, ClassNotFoundException{
		return adminDao.queryUserList(level);
	}
	
	public String updatePermission(List<String> addItem ,List<String> updateItem , Map<String,PermissionBean> dataMap ) throws ClassNotFoundException, SQLException{
		return adminDao.updatePermission(addItem, updateItem, dataMap);
	}
	
	public String newAccount(String acc,Map<String,Object> owner) throws SQLException, ClassNotFoundException {
		return adminDao.newAccount(acc, owner);
	}
	
	
	public String changePassword(String account,String oldPassword,String newPassword) throws SQLException, ClassNotFoundException {
		return adminDao.changePassword(account,oldPassword, newPassword);
	}

	public AdminDao getAdminDao() {
		return adminDao;
	}

	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}
	
	
}
