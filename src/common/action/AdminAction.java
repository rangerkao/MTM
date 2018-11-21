package common.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import common.bean.PermissionBean;
import common.service.AdminService;

@Controller
public class AdminAction extends BaseAction{
	
	@Resource
	AdminService adminService;
	
	String queryLevel;
	
	//權限管理清單
	public String queryPermission() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			List<PermissionBean> result = adminService.queryPermission((String)account);
			//actionLog("queryPermission",null);
			return setSuccess(result);
		} catch (ClassNotFoundException e) {
			return errorHandle(e);
		} catch (SQLException e) {
			return errorHandle(e);
		}
	}
	
	public String queryUser() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");

			return setSuccess(m);
		} catch (Exception e) {
			return errorHandle(e);
		}
	}
	
	
	public String queryUserList() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");
			String userLevel = (String) m.get("USER_LEVEL");
			
			
			int level = -1;
			if(queryLevel!=null && !"".equals(queryLevel) && !"[]".equals(queryLevel))
				level = Integer.valueOf(queryLevel);
			
			//管理者功能
			if(level == 1 && !userLevel.equals("0")) {
				
				Map<String,String> result = new HashMap<String,String>();
				return setSuccess(result);
			}
			
			Map<String,String> result = adminService.queryUserList(level);
			//actionLog("queryUserList",null);
			return setSuccess(result);
		} catch (ClassNotFoundException e) {
			return errorHandle(e);
		} catch (SQLException e) {
			return errorHandle(e);
		}
	}
	
	//更新使用者權限
	public String updatePermission() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			
			
			Map m = (Map) sessionMap.getAttribute("permission");
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			
			//層級3為最低階使用者，不可更改任何權限(含自身)
			if(level==null || Integer.parseInt(level)>=3) {
				return setFail("無權進行此操作");
			}
			
			JSONObject jo = jsonToJSONObject(input);
			
			JSONArray jaUpdate = jo.getJSONArray("updateItem");
			JSONArray jaAdd = jo.getJSONArray("addItem");
			JSONArray jaList = jo.getJSONArray("dataList");

			Map<String,PermissionBean> dataMap = new HashMap<String,PermissionBean>();
			for(Object j : jaList) {
				PermissionBean p = jsonObjectToPermissionBean((JSONObject)j);
				if(p.getSubsidiaryid() == null)
					p.setSubsidiaryid(subsidiaryids);
				dataMap.put(p.getAccount(), p);
			}

			List<String> updateItem = new ArrayList<String>();
			
			for(Object j : jaUpdate) {
				updateItem.add((String) j);	
			}
			
			List<String> addItem = new ArrayList<String>();
			for(Object j : jaAdd) {
				addItem.add((String) j);	
			}
			
			
			Map<String,String> param = new HashMap<String,String>();
			for(String account : updateItem) {
				PermissionBean p = dataMap.get(account);
				param.put(account, "Search="+p.getSearch()+",Suspend="+p.getSuspend()+",Resume="+p.getResume()+",Terminate="+p.getTerminate()+",Reset="+p.getReset());
			}
			actionLog("Update Permission",beanToJSONObject(param));
			
			String result = adminService.updatePermission(addItem,updateItem,dataMap);
			if("success".equalsIgnoreCase(result))
				return setSuccess(adminService.queryPermission((String)account));
			else 
				return setFail(result);
		} catch (ClassNotFoundException e) {
			return ajaxErrorHandle(e);
		} catch (SQLException e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String newAccount() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			
			
			Map m = (Map) sessionMap.getAttribute("permission");
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			if(level==null || Integer.parseInt(level)>=3) {
				return setFail("無權進行此操作");
			}
			
			JSONObject jo = jsonToJSONObject(input);
			
			String newAcc = jo.getString("account");
			
			actionLog("Create Account",newAcc);
			
			String result = adminService.newAccount(newAcc, m);
			
			if("success".equalsIgnoreCase(result))
				return setSuccess(adminService.queryPermission((String)account));
			else 
				return setFail(result);
			

		} catch (ClassNotFoundException e) {
			return ajaxErrorHandle(e);
		} catch (SQLException e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String changePassword() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			
			
			Map m = (Map) sessionMap.getAttribute("permission");
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			if(level==null) {
				return setFail("無權進行此操作");
			}
			
			String result = null;
			
			JSONObject jo = jsonToJSONObject(input);
			
		
			String acc = null;
			String action = null;
			//Reset
			if(jo.has("account")) {
				
				action = "Reset Password.";
				
				acc = jo.getString("account");
				//newPass = acc;
				result = adminService.changePassword(acc,null, acc);
			}else {
				action = "Change Password.";
				acc = (String)account;
				String origin = jo.getString("origin");
				String newPass = jo.getString("new");
				System.out.print(origin+":"+newPass);
				result = adminService.changePassword(acc,origin, newPass);
			}
			
			Map<String,String> param = new HashMap<String,String>();
			param.put("Account", acc);
			param.put("Action", action);
			actionLog("Change Password",beanToJSONObject(param));

			if("success".equalsIgnoreCase(result))
				return setSuccess(result);
			else 
				return setFail(result);

		} catch (ClassNotFoundException e) {
			return ajaxErrorHandle(e);
		} catch (SQLException e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String queryUserAction() {
		try {
			if(!checkSession()) return setHome("Please login first.");
			//actionLog("resetPassword",input);
			
			Map m = (Map) sessionMap.getAttribute("permission");
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			if(level==null) {
				return setFail("無權進行此操作");
			}
			
			List<Map<String,String>> userAction = new ArrayList<Map<String,String>>();
			
			boolean test = false;
			if("1".equalsIgnoreCase((String) m.get("RESET"))||test) {
				Map<String,String> sm = new HashMap<String,String>();
				sm.put("label", "Reset-speed");
				sm.put("value", "00");
				userAction.add(sm);			
			}
			if("1".equalsIgnoreCase((String) m.get("RESUME"))||test) {
				Map<String,String> sm = new HashMap<String,String>();
				sm.put("label", "Activated");
				sm.put("value", "02");
				userAction.add(sm);	
			}
			if("1".equalsIgnoreCase((String) m.get("SUSPEND"))||test) {
				Map<String,String> sm = new HashMap<String,String>();
				sm.put("label", "De-activated");
				sm.put("value", "01");
				userAction.add(sm);	
			}
			if("1".equalsIgnoreCase((String) m.get("TERMINATE"))||test) {
				Map<String,String> sm = new HashMap<String,String>();
				sm.put("label", "Retired");
				sm.put("value", "99");
				userAction.add(sm);	
			}
			
			return setSuccess(userAction);

		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}

	public PermissionBean jsonObjectToPermissionBean(JSONObject j) {
		PermissionBean p = new PermissionBean();
		p.setAccount(j.getString("account"));
		//p.setLevel(j.getInt("level"));
		//p.setOwner(j.isNull("owner")?"":j.getString("owner"));
		//if(j.has("subsidiaryids"))
			//p.setSubsidiaryid(j.getString("subsidiaryids"));
		//p.setPassword(j.getString("password"));
		p.setResume(j.getBoolean("resume"));
		p.setSuspend(j.getBoolean("suspend"));
		p.setSearch(j.getBoolean("search"));
		p.setTerminate(j.getBoolean("terminate"));
		p.setReset(j.getBoolean("reset"));
		return p;
	}
	
	
	
	
	
	
	
	public String getQueryLevel() {
		return queryLevel;
	}

	public void setQueryLevel(String queryLevel) {
		this.queryLevel = queryLevel;
	}

	public AdminService getAdminService() {
		return adminService;
	}
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}
	
	
	

}
