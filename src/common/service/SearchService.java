package common.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import common.bean.DataBean;
import common.dao.SearchDao;
import common.action.Excel;

@Service
public class SearchService {
	@Resource
	SearchDao searchDao;
	
	@Resource
	Excel excel;
	
	public List<DataBean> queryDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		return searchDao.queryDatas(subsidiaryids, startPage, onePage, orderby, asc,condition);
	}
	
	public int queryCount(String subsidiaryids,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		return searchDao.queryCount(subsidiaryids,condition);
	}
	
	public InputStream  exportDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition,List<Map<String,Object>> head) throws Exception{
		
		List<DataBean> data = searchDao.queryDatas(subsidiaryids, startPage, -1, orderby, asc,condition);

		XSSFWorkbook wb = excel.createExcelByBean(head, data);

		System.out.println("get excel data end.");
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
        wb.write(os);  
        byte[] fileContent = os.toByteArray();  
        ByteArrayInputStream is = new ByteArrayInputStream(fileContent);  
		return is;
	}
	
	public List<DataBean> queryUsageDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition) throws Exception {
		return searchDao.queryUsageDatas(subsidiaryids, startPage, onePage, orderby, asc,condition);
	}
	
	public int queryUsageCount(String subsidiaryids,Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		return searchDao.queryUsageCount(subsidiaryids,condition);
	}
	
	public InputStream  exportUsageDatas(String subsidiaryids,int startPage,int onePage,String orderby,boolean asc,Map<String,Object> condition,List<Map<String,Object>> head) throws Exception{
		
		List<DataBean> data = searchDao.queryUsageDatas(subsidiaryids, startPage, -1, orderby, asc,condition);

		XSSFWorkbook wb = excel.createExcelByBean(head, data);

		System.out.println("get excel data end.");
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
        wb.write(os);  
        byte[] fileContent = os.toByteArray();  
        ByteArrayInputStream is = new ByteArrayInputStream(fileContent);  
		return is;
	}
	
	public List<DataBean> queryAllDatas(String subsidiaryids, Map<String,Object> condition) throws ClassNotFoundException, SQLException, ParseException {
		return searchDao.queryAllDatas(subsidiaryids, condition);
	}

	public SearchDao getSearchDao() {
		return searchDao;
	}

	public void setSearchDao(SearchDao searchDao) {
		this.searchDao = searchDao;
	}
	
	

}
