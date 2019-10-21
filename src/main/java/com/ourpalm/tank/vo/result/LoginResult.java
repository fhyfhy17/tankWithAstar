package com.ourpalm.tank.vo.result;

import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.vo.LoginUserInfo;

public class LoginResult {

	private ROLE_MSG.LOGIN_STATE state;
	private String info = "";
	private LoginUserInfo userInfo;
	
	
	public ROLE_MSG.LOGIN_STATE getState() {
		return state;
	}
	public void setState(ROLE_MSG.LOGIN_STATE state) {
		this.state = state;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public LoginUserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(LoginUserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
