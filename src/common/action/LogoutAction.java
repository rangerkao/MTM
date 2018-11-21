package common.action;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;

@Controller
public class LogoutAction extends BaseAction{

	public String logout(){  
		System.out.println("Process Logout");
		
		if(checkSession()) {
			try {
				actionLog("logout",null);
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
}
