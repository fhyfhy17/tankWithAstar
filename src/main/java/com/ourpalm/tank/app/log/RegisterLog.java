package com.ourpalm.tank.app.log;

import com.ourpalm.tank.app.GameContext;

public class RegisterLog extends AbstractLog {
	
	/**日志ID*/
	private static final String logId = "10006";

	private String userip;
	private int time;
	private String domain;
	private int worldid;
	private String opuid;
	private String opopenid;

	
	@Override
	public String getLogId() {
		return logId;
	}

	@Override
	public void createLog() {}
	
	@Override
	public String getUrlParams(){
		String params = "report_register.php?version=1&appid="+GameContext.getTxAppId()+ 
				"&userip="+this.getUserip()+"&time="+(int)(System.currentTimeMillis() / 1000);
		params += "&domain="+this.getDomain()+"&worldid="+this.getWorldid()+"&opuid="+this.getOpuid()+"&opopenid="+this.getOpopenid();
		return params;
	}

	public String getOpuid() {
		return opuid;
	}

	public void setOpuid(String opuid) {
		this.opuid = opuid;
	}

	public String getOpopenid() {
		return opopenid;
	}

	public void setOpopenid(String opopenid) {
		this.opopenid = opopenid;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getWorldid() {
		return worldid;
	}

	public void setWorldid(int worldid) {
		this.worldid = worldid;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}
	
}
