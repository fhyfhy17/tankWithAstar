package com.ourpalm.tank.app.platform;

public class ChatInfo {

	private int areaId;
	private String userId;
	private int roleId;
	private String roleName;
	private int chatChannel;		//聊天频道
	private String chatInfo;		//聊天内容
	private String chatTime;		//聊天时间(格式: yyyy-MM-dd HH:mm:ss)
	
	
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public int getChatChannel() {
		return chatChannel;
	}
	public void setChatChannel(int chatChannel) {
		this.chatChannel = chatChannel;
	}
	public String getChatInfo() {
		return chatInfo;
	}
	public void setChatInfo(String chatInfo) {
		this.chatInfo = chatInfo;
	}
	public String getChatTime() {
		return chatTime;
	}
	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}
}
