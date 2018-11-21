package common.action;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Controller;

import common.service.LoginService;

@Controller
public class LoginAction extends BaseAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String password;
	String account;
	
	@Resource
	LoginService loginService;
	
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
			
			permission = loginService.login(account, password);
			if(permission.size()==0) {
				addActionError("Account not Exists or Password Error.");
				return "input";
			}else {
				session.put("account", account);
				//session.put("s2t.password", user.getPassword());
				session.put("permission", permission);
				super.account = account;
				actionLog("Logging",null);
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

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
	
	
}
