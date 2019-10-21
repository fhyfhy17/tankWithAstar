package com.ourpalm.tank.vo;

/**
 * 用户中心验证消息
 * 
 * @author wangkun
 *
 */
public class LoginCheckReq {

	private String interfaceId = "0002";
	private String tokenId = "";
	
	
	public String getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
}
