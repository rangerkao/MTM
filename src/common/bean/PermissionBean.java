package common.bean;

public class PermissionBean {

	
	String account;
	String password;
	Integer level;
	String owner;
	String subsidiaryid;

	Boolean search;
	Boolean suspend;
	Boolean resume;
	Boolean terminate;
	Boolean reset;
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getSubsidiaryid() {
		return subsidiaryid;
	}
	public void setSubsidiaryid(String subsidiaryid) {
		this.subsidiaryid = subsidiaryid;
	}
	public Boolean getSearch() {
		return search;
	}
	public void setSearch(Boolean search) {
		this.search = search;
	}
	public Boolean getSuspend() {
		return suspend;
	}
	public void setSuspend(Boolean suspend) {
		this.suspend = suspend;
	}
	public Boolean getResume() {
		return resume;
	}
	public void setResume(Boolean resume) {
		this.resume = resume;
	}
	public Boolean getTerminate() {
		return terminate;
	}
	public void setTerminate(Boolean terminate) {
		this.terminate = terminate;
	}
	public Boolean getReset() {
		return reset;
	}
	public void setReset(Boolean reset) {
		this.reset = reset;
	}
	
}
