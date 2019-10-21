package com.ourpalm.tank.vo;

/**
 * 用户中心验证返回消息
 * 
 * @author wangkun
 *
 */
public class LoginCheckResp {

	private String bindingMode;
	private String loginType;
	private String status;
	private String reset;
	private String desc;
	private String tokenId;
	private LoginUserInfo userInfo;
	
	public String getBindingMode() {
		return bindingMode;
	}
	public void setBindingMode(String bindingMode) {
		this.bindingMode = bindingMode;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReset() {
		return reset;
	}
	public void setReset(String reset) {
		this.reset = reset;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public LoginUserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(LoginUserInfo userInfo) {
		this.userInfo = userInfo;
	} 
}
