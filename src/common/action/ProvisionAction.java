package common.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import common.bean.DataBean;
import common.bean.PermissionBean;
import common.service.ProvisionService;

@Controller
public class ProvisionAction extends BaseAction {

	@Resource
	ProvisionService provisionService;
	
	public String executeProvision() {
		
		System.out.println("recevies input "+input);
		String result = "Provision finished.";
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");
			
			
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			
			JSONObject j = new JSONObject(input);
			
			
			String action = j.getString("action");
			
			if(("00".equals(action) && "0".equals(m.get("RESET")))||
					("01".equals(action) && "0".equals(m.get("SUSPEND")))||
					("02".equals(action) && "0".equals(m.get("RESUME")))||
					("99".equals(action) && "0".equals(m.get("TERMINATE"))))
				return setFail("Need Permission.");
			
			
			
			
			
			JSONArray imsis = j.getJSONArray("imsis");
			List<Object> imsiList = imsis.toList();
			
			
			int actionInt = Integer.valueOf(action);
			
			String sa = null;
			
			switch(actionInt){
			case 0:
				sa = "Reset-speed";
				break;
			case 1:
				sa = "De-activated";
				break;
			case 2:
				sa = "Activated";
				break;
			case 99:
				sa = "Retired";
				break;
			default:
				return setFail("No Data.");
			}
			
			/*if("00".equals(action)) {
				sa = "Reset-speed";
			}else if("01".equals(action)) {
				sa = "De-activated";
			}else if("02".equals(action)) {
				sa = "Activated";
			}else if("99".equals(action)) {
				sa = "Retired";
			}*/
			
			Map<String,String> param = new HashMap<String,String>();			
			//String s = "";
			int i = 0;
			for(Object imsi:imsiList) {
				i++;
				param.put(""+i, (String) imsi);
				if(i==50) {
					actionLog("Execute "+sa,beanToJSONObject(param));
					param.clear();
					i=0;
				}
				
				/*if(i==0) {
					i++;
					//s = (String) imsi;
					
				}else {
					i++;
					//s += ","+imsi;
				}
				
				if(i==50) {
					actionLog("Execute Provision",s);
					//s = "";
					i = 0;
				}*/
			}
			/*if(i!=0) {
				actionLog("Execute "+sa,s);
			}*/
			
			if(i!=0) {
				actionLog("Execute "+sa,beanToJSONObject(param));
			}

			provisionService.executeProvision(imsiList, action);
			
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}

	public ProvisionService getProvisionService() {
		return provisionService;
	}

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}
}
