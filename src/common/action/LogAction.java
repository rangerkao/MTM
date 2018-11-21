package common.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import common.bean.DataBean;
import common.bean.LogBean;
import common.service.LogService;

@Controller
public class LogAction extends BaseAction {
	
	String password;
	String account;
	
	@Resource
	LogService logService;
	
	//在execute之前執行
	public void validate() {
		clearFieldErrors();
		clearActionErrors();
		if (account == null || "".equals(account))
			addFieldError("acc", "帳號為必填，請輸入帳號");
		if (password == null || "".equals(password))
			addFieldError("psw", "密碼為必填，請輸入密碼");
	}
	
	public String login() {
		
		System.out.println("getRequestURI:"+ServletActionContext.getRequest().getHeader("referer"));
		
		if(ServletActionContext.getRequest().getHeader("referer") == null)
			return "input";
		
		System.out.println("Account:"+account+";Password:"+password);
		
		Map<String, String> permission;
		try {
			
			permission = logService.login(account, password);
			if(permission.size()==0) {
				addActionError("Account not Exists or Password Error.");
				return "input";
			}else {
				session.put("account", account);
				//session.put("s2t.password", user.getPassword());
				session.put("permission", permission);
				super.account = account;
				actionLog("Login",null);
				return SUCCESS;
			}
		} catch (SQLException e) {
			return errorHandle(e);
		} catch (ClassNotFoundException e) {
			return errorHandle(e);
		} catch (Exception e) {
			return errorHandle(e);
		}
	}
	
	
	public String queryLogDatas() {
		
		System.out.println("recevies input "+input);
		List<LogBean> result = null;
		try {
			if(!checkSession()) return setHome("Please login first.");
			//actionLog("queryLogDatas",input);
			Map m = (Map) sessionMap.getAttribute("permission");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			
			JSONObject j = new JSONObject(input);
			int startPage = j.getInt("startPage");
			int onePage = j.getInt("onePage");
			String orderby = j.isNull("orderby")?null:j.getString("orderby");
			Boolean asc = "ASC".equalsIgnoreCase(j.isNull("asc")?null:j.getString("asc"));
			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(!j.isNull("condition")&& j.get("condition").toString().startsWith("[")) {
				JSONArray c = j.getJSONArray("condition");
				condition = processCondition(c);
			}
			
			condition.put("account", account);
			
			result = logService.queryLogDatas(startPage, onePage, orderby, asc,condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String queryLogCount() {
		System.out.println("recevies input "+input);
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(input!=null && input.startsWith("[")) {
				JSONArray c = new JSONArray(input);
				condition = processCondition(c);
			}
			
			condition.put("account", account);
			
			int result = 0;
			result = logService.queryLogCount(condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String logout(){  
		System.out.println("Process Logout");
		
		if(checkSession()) {
			try {
				actionLog("Logout",null);
			} catch (ClassNotFoundException e) {
			} catch (SQLException e) {
			}
			getSession();
		    if(session!=null){  
		    	 //session.invalidate();
		    	session.clear();
		    	 System.out.println("session destroied");
		    }  
		}else{
			System.out.println("no session.");
		}
		
	    return SUCCESS;  
	}
	
	
	public Map<String,Object> processCondition(JSONArray c){
		Map<String,Object> condition = new HashMap<String,Object>();
		for(int i = 0 ; i < c.length();i++){
			JSONObject j = c.getJSONObject(i);
			Iterator k = j.keys();
			while(k.hasNext()) {
				String key = (String) k.next();
				condition.put(key, j.getString(key));
			}
		}
		return condition;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public LogService getLogService() {
		return logService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}  
	
	
}
