package com.ourpalm.core.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 保证服务器启动时，按顺序加载服务
 * @author wangkun
 *
 */
public class ServerHolder {
	
	/** 系统功能初始化加载 */
	private Map<String,Service> initServiceMap = new HashMap<String,Service>();
	
	/** 服务端监听功能加载 */
	private Map<String,Service> serverMap = new HashMap<String,Service>();
	
	/**服务器成功启动后触发*/
	private Map<String,Service> startedServiceMap = new HashMap<String,Service>();
	
	public Map<String, Service> getInitServiceMap() {
		return initServiceMap;
	}

	public void setInitServiceMap(Map<String, Service> initServiceMap) {
		this.initServiceMap = initServiceMap;
	}

	public Map<String, Service> getServerMap() {
		return serverMap;
	}

	public void setServerMap(Map<String, Service> serverMap) {
		this.serverMap = serverMap;
	}

	public Map<String, Service> getStartedServiceMap() {
		return startedServiceMap;
	}

	public void setStartedServiceMap(Map<String, Service> startedServiceMap) {
		this.startedServiceMap = startedServiceMap;
	}

	
}
