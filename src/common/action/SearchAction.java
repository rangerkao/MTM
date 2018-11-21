package common.action;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import common.bean.DataBean;
import common.bean.PermissionBean;
import common.service.SearchService;

@Controller
public class SearchAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	SearchService searchService;
	
	public String queryDatas() {
		
		System.out.println("recevies input "+input);
		List<DataBean> result;
		try {
			if(!checkSession()) return setHome("請先進行登入.");
			
			
			Map m = (Map) sessionMap.getAttribute("permission");
			
			JSONObject j = new JSONObject(input);
			int startPage = j.getInt("startPage");
			int onePage = j.getInt("onePage");
			String orderby = j.isNull("orderby")?null:j.getString("orderby");
			Boolean asc = "ASC".equalsIgnoreCase(j.isNull("asc")?null:j.getString("asc"));
			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(!j.isNull("condition")&& j.get("condition").toString().startsWith("[")) {
				JSONArray c = j.getJSONArray("condition");
				condition = processCondition(c);
				/*if(!c.isNull("ICCID")) {
					condition.put("ICCID", c.getString("ICCID"));
				}
				if(!c.isNull("Status")) {
					condition.put("Status", c.getString("Status"));
				}*/
			}
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			Map<String,String> param = new HashMap<String,String>();
			if("0".equals(level)) {
				subsidiaryids = (String) condition.get("subsidiaryid");
				
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
				param.put("SUBSIDIARYIDS", subsidiaryids);
			}
			
			for(String key : condition.keySet()) {
				param.put(key, (String) condition.get(key));
			}

			actionLog("Query Data",beanToJSONObject(param));
	
			
			result = searchService.queryDatas(subsidiaryids, startPage, onePage, orderby, asc,condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String queryCount() {
		System.out.println("recevies input "+input);
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");
			if("0".equals(m.get("SEARCH")))
				return setFail("Need Permission.");
			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(input!=null && input.startsWith("[")) {
				JSONArray c = new JSONArray(input);
				condition = processCondition(c);
				/*if(!c.isNull("ICCID")) {
					condition.put("ICCID", c.getString("ICCID"));
				}
				if(!c.isNull("Status")) {
					condition.put("Status", c.getString("Status"));
				}*/
			}
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			
			if("0".equals(level) ) {
				subsidiaryids = (String) condition.get("subsidiaryid");
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			}
			
			int result = searchService.queryCount(subsidiaryids,condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	private InputStream excelStream;  //輸出變量
	private String excelFileName; //下載文件名稱
	
	public String exportDatas(){
		downloadFinashed = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		excelFileName = "DeviceExport_"+sdf.format(new Date())+".xlsx";
		
		try {
			if(!checkSession()) return setHome("Please login first.");
		
			Map m = (Map) sessionMap.getAttribute("permission");

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
			
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			
			Map<String,String> param = new HashMap<String,String>();
			
			if("0".equals(level) ) {
				subsidiaryids = (String) condition.get("subsidiaryid");
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
				param.put("SUBSIDIARYIDS", subsidiaryids);
			}
			
			
			
			for(String key : condition.keySet()) {
				param.put(key, (String) condition.get(key));
			}
			actionLog("Export Data",beanToJSONObject(param));
			
			List<Map<String,Object>> head = new ArrayList<Map<String,Object>>();
			if(!j.isNull("head")&& j.get("head").toString().startsWith("[")) {
				JSONArray c = j.getJSONArray("head");
				for(int i = 0 ; i<c.length() ; i++) {
					Map<String,Object> mc = new HashMap<String,Object>();
					JSONObject jo = c.getJSONObject(i);
					
					mc.put("name", jo.get("name"));
					mc.put("value", jo.get("value"));
					String width = (String) jo.get("width");
					if(width!=null)
					mc.put("width", width.replace("px", "").replace("%", ""));
					head.add(mc);
				}
			}
			
			InputStream in = searchService.exportDatas(subsidiaryids,startPage,onePage,orderby,asc,condition,head);
			
			if(in==null)
				throw new Exception("File not Exist.");
			setExcelStream(in);
		} catch (Exception e) {
			errorHandle(e);
		}
		downloadFinashed = true;

		return SUCCESS;
	}
	
	public String queryUsageDatas() {
		
		System.out.println("recevies input "+input);
		List<DataBean> result = null;
		try {
			if(!checkSession()) return setHome("Please login first.");
			//actionLog("exportDatas",input);
			Map m = (Map) sessionMap.getAttribute("permission");
			if("0".equals(m.get("SEARCH")))
				return setFail("Need Permission.");
			
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
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			
			Map<String,String> param = new HashMap<String,String>();
			
			if("0".equals(level) ) {
				subsidiaryids = (String) condition.get("subsidiaryid");
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
				param.put("SUBSIDIARYIDS", subsidiaryids);
			}
			
			
			
			for(String key : condition.keySet()) {
				param.put(key, (String) condition.get(key));
			}
			actionLog("Query Usage Data",beanToJSONObject(param));
			
			result = searchService.queryUsageDatas(subsidiaryids, startPage, onePage, orderby, asc,condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String queryUsageCount() {
		System.out.println("recevies input "+input);
		try {
			if(!checkSession()) return setHome("Please login first.");
			Map m = (Map) sessionMap.getAttribute("permission");
			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(input!=null && input.startsWith("[")) {
				JSONArray c = new JSONArray(input);
				condition = processCondition(c);
			}
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			
			if("0".equals(level) ) {
				subsidiaryids = (String) condition.get("subsidiaryid");
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			}
			
			int result = 0;
			result = searchService.queryUsageCount(subsidiaryids,condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
	}
	
	public String exportUsageDatas(){
		downloadFinashed = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		excelFileName = "DataExport_"+sdf.format(new Date())+".xlsx";
		
		try {
			if(!checkSession()) return setHome("Please login first.");
			//actionLog("exportUsageDatas",input);
			Map m = (Map) sessionMap.getAttribute("permission");

			if("0".equals(m.get("SEARCH")))
				return setFail("Need Permission.");
			
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
			
			
			String level = (String) m.get("USER_LEVEL");
			String subsidiaryids = null;
			
			Map<String,String> param = new HashMap<String,String>();
			
			if("0".equals(level) ) {
				subsidiaryids = (String) condition.get("subsidiaryid");
			}else {
				subsidiaryids = m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
				param.put("SUBSIDIARYIDS", subsidiaryids);
			}
			
			
			
			for(String key : condition.keySet()) {
				param.put(key, (String) condition.get(key));
			}
			actionLog("Export Usage  Data",beanToJSONObject(param));
			
			
			List<Map<String,Object>> head = new ArrayList<Map<String,Object>>();
			if(!j.isNull("head")&& j.get("head").toString().startsWith("[")) {
				JSONArray c = j.getJSONArray("head");
				for(int i = 0 ; i<c.length() ; i++) {
					Map<String,Object> mc = new HashMap<String,Object>();
					JSONObject jo = c.getJSONObject(i);
					
					mc.put("name", jo.get("name"));
					mc.put("value", jo.get("value"));
					String width = (String) jo.get("width");
					if(width!=null)
					mc.put("width", width.replace("px", "").replace("%", ""));
					head.add(mc);
				}
			}
			
			InputStream in = searchService.exportUsageDatas(subsidiaryids,startPage,onePage,orderby,asc,condition,head);
			
			if(in==null)
				throw new Exception("File not Exist.");
			setExcelStream(in);
		} catch (Exception e) {
			errorHandle(e);
		}
		downloadFinashed = true;

		return SUCCESS;
	}
	
	public String queryAllDatas() {
		
		System.out.println("recevies input "+input);
		List<DataBean> result;
		try {
			if(!checkSession()) return setHome("Please login first.");
			//actionLog("queryAllDatas",input);
			Map m = (Map) sessionMap.getAttribute("permission");
			if("0".equals(m.get("SEARCH")))
				return setFail("Need Permission.");
			
			String subsidiaryids =  m.get("SUBSIDIARYIDS")!=null ? (String) m.get("SUBSIDIARYIDS") : null;
			JSONObject j = new JSONObject(input);			
			Map<String,Object> condition = new HashMap<String,Object>();
			if(!j.isNull("condition")&& j.get("condition").toString().startsWith("[")) {
				JSONArray c = j.getJSONArray("condition");
				condition = processCondition(c);
				/*if(!c.isNull("ICCID")) {
					condition.put("ICCID", c.getString("ICCID"));
				}
				if(!c.isNull("Status")) {
					condition.put("Status", c.getString("Status"));
				}*/
			}
			
			Map<String,String> param = new HashMap<String,String>();
			param.put("SUBSIDIARYIDS", subsidiaryids);
			for(String key : condition.keySet()) {
				param.put(key, (String) condition.get(key));
			}
			actionLog("Query all data",beanToJSONObject(param));
			
			result = searchService.queryAllDatas(subsidiaryids, condition);
			return setSuccess(result);
		} catch (Exception e) {
			return ajaxErrorHandle(e);
		}
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
	
	boolean downloadFinashed = true;
	public String getDownloadFlag() {
		System.out.println("getting downloadFlag "+this.downloadFinashed);
		setSuccess(this.downloadFinashed);
		
		return SUCCESS;
	}
	
	public SearchService getSearchService() {
		return searchService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public InputStream getExcelStream() {
		return excelStream;
	}

	public void setExcelStream(InputStream excelStream) {
		this.excelStream = excelStream;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
	
	
	

}
