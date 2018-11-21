package common.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

import common.cache.CacheAction;


public class BaseAction extends ActionSupport implements SessionAware   {
	
	Logger logger = CacheAction.logger;
	
	String input;
	String result;
	Object account;
	
	public String setSuccess(Object data){
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("data", data);
		result = beanToJSONObject(map);
		return SUCCESS;
	}
	
	public String setFail(String msg){
		Map<String,String> map = new HashMap<String,String>();
		map.put(ERROR, msg);
		result = beanToJSONObject(map);
		return SUCCESS;
	}
	
	public String setHome(String msg){
		Map<String,String> map = new HashMap<String,String>();
		map.put(INPUT, msg);
		result = beanToJSONObject(map);
		return SUCCESS;
	}
	
	protected String beanToJSONArray(List list){
		JSONArray jo = (JSONArray) JSONObject.wrap(list);
		return jo.toString();
	}
	protected static String beanToJSONObject(Object object){
		JSONObject jo = (JSONObject) JSONObject.wrap(object);
		return jo.toString();
	}
	protected static JSONObject jsonToJSONObject(String json){
		return new JSONObject(json);
	}
	
	public String errorHandle(Exception e){
		e.printStackTrace();
		StringWriter s = new StringWriter();
		e.printStackTrace(new PrintWriter(s));
		CacheAction.sendMail("k1988242001@gmail.com","MTM Exception Error!",new Date()+"\n"+s);
		addActionError(s.toString());
		return "input";
	}
	
	public String ajaxErrorHandle(Exception e){
		e.printStackTrace();
		StringWriter s = new StringWriter();
		e.printStackTrace(new PrintWriter(s));
		CacheAction.sendMail("k1988242001@gmail.com","MTM Exception Error!",new Date()+"\n"+s);
		return setFail(s.toString());
	}
	
	HttpSession sessionMap;
	public boolean checkSession() {
		
		sessionMap = getSession();
		if(sessionMap == null)
			return false;
		
		account = sessionMap.getAttribute("account");
		if(account ==null)
			return false;
		return true;
	}
	
	@Override  
    public String execute() {
		System.out.println(session.get("acccount")+" logined.");
		
		return SUCCESS;  
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	protected SessionMap session; 
	
	@Override
	public void setSession(Map<String, Object> session) {
		 this.session = (SessionMap) session; 
	}
	
	public HttpSession getSession(){
		
		return ServletActionContext.getRequest().getSession(false);  
	}
	
	
    protected HttpServletRequest request; 
    public void setServletRequest(HttpServletRequest request) { 
        this.request = request; 
    } 
    
	public String getSessionStatus() {
		if(!checkSession()) 
			return setHome("請先進行登入.");
		
		
		return setSuccess("Normal");
	}
	
	protected void actionLog(String action,String params) throws ClassNotFoundException, SQLException {
		CacheAction.actionLog(account.toString(),action,params);
	}
    
	
	
}
