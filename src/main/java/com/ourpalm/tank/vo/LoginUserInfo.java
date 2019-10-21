package com.ourpalm.tank.vo;

import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;

/**
 * 用户中心返回用户信息
 * 
 * @author wangkun
 *
 */
public class LoginUserInfo {
	
	private String id;
	private String userName;
	private String email;
	private String returnJson;
	private PFUserInfo pfUserInfo;
	private PFYellowUserInfo pfYellowUserInfo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getReturnJson() {
		return returnJson;
	}
	public void setReturnJson(String returnJson) {
		this.returnJson = returnJson;
	}
	public PFUserInfo getPfUserInfo() {
		return pfUserInfo;
	}
	public void setPfUserInfo(PFUserInfo pfUserInfo) {
		this.pfUserInfo = pfUserInfo;
	}
	public PFYellowUserInfo getPfYellowUserInfo() {
		return pfYellowUserInfo;
	}
	public void setPfYellowUserInfo(PFYellowUserInfo pfYellowUserInfo) {
		this.pfYellowUserInfo = pfYellowUserInfo;
	}
	
}
