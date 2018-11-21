package common.action;

import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

@Controller
public class IndexAction extends ActionSupport  {

	public String index() {
		return SUCCESS ;
	}
	
	public String admin() {return "admin";}
	public String search() {return "search";}
	public String search2() {return "search2";}
	public String logQuery() {return "logQuery";}
	public String welcome() {return "welcome";}
}
