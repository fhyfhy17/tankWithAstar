package com.ourpalm.core.dao.redis.client;

public class ShardedParam {

	private String ip;
	private int port;
	private String passwords;
	
	public ShardedParam(String ip, int port, String passwords){
		this.ip = ip;
		this.port = port;
		this.passwords = passwords;
	}
	
	
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public String getPasswords() {
		return passwords;
	}
}
