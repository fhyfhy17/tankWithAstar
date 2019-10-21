package com.ourpalm.tank.domain;

public class GmMail {
	private int id;
	//过期时间
	private long expireTime;
	//渠道
	private String serviceId;
	//分区
	private int areaId;
	//是否发送给新玩家
	private boolean toNewPlayer; 
	//创建时间
	private long createTime;
	
	private Mail mail;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	
	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public boolean isToNewPlayer() {
		return toNewPlayer;
	}

	public void setToNewPlayer(boolean toNewPlayer) {
		this.toNewPlayer = toNewPlayer;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	}
