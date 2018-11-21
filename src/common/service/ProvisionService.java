package common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import common.dao.ProvisionDao;

@Service
public class ProvisionService{
	
	@Resource
	ProvisionDao provisionDao;
	
	public void executeProvision(List<Object> imsis,String request) throws Exception {
		provisionDao.provision(imsis, request);
	}

	public ProvisionDao getProvisionDao() {
		return provisionDao;
	}

	public void setProvisionDao(ProvisionDao provisionDao) {
		this.provisionDao = provisionDao;
	}
}
