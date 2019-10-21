package com.ourpalm.tank.app.log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import net.gamebean.prism.PalmLogSender;
import net.gamebean.prism.service.Log;
import net.gamebean.prism.service.RoleInfo;
import net.gamebean.prism.service.UserInfo;

public abstract class AbstractLog {
	
	protected Logger logger = LogCore.runtime;
	
	/** 角色ID*/
	private Integer roleId;
	
	/** 日志数据集合 */
	private Map<String,String> dataMap = new HashMap<>();
	
	/**
	 * 获取日志ID
	 * @return
	 */
	public abstract String getLogId();
	
	/**
	 * 创建日志
	 * @return
	 */
	public abstract void createLog();
	
	/**
	 * 获取参数
	 * @return
	 */
	public abstract String getUrlParams();
	
	/**
	 * 发送日志
	 */
	protected void sendLog() throws Exception{
		//发送日志
		this.postHttp();
	};
	
	protected void postHttp(){
		
		String reqMsg = this.getUrlParams();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try{
			URL url = new URL(GameContext.getReportUrl()+reqMsg);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			
			if(logger.isDebugEnabled()){
				logger.debug("向用户中心登录验证, URL:{}, 请求 : {}", GameContext.getReportUrl(), reqMsg);
			}
			
			HttpPost httppost = new HttpPost(uri);
			
			CloseableHttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			int status = response.getStatusLine().getStatusCode();
			if(status < 200 || status >= 300){
				logger.error("httStatus=" + status + " targetUrl=" + GameContext.getReportUrl());
			}
			
			InputStream input = resEntity.getContent();
			byte[] data = new byte[input.available()];
			input.read(data);
			
			String respMsg = new String(data);
			if(logger.isDebugEnabled()){
				logger.debug("HTTP 返回: {}", respMsg);
			}
			
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
			}
		}
		
	}
	
	/**
	 * 发送日志
	 * @param logArr 日志集合
	 */
	protected void sendLog(List<Log> logArr) throws Exception{
		RoleAccount roleAccount = GameContext.getUserApp().getRoleAccount(roleId);
		if(roleAccount == null)
			return;
		String serviceCode = roleAccount.getServiceCode();
		UserInfo userInfo = getUserInfo(roleAccount);
		RoleInfo roleInfo = getRoleInfo(roleAccount);
		if(Util.isEmpty(serviceCode) || Util.isEmpty(logArr)
				|| userInfo == null || roleInfo == null)
			return;
		//发送日志
		PalmLogSender.sendLogList(serviceCode, userInfo, roleInfo, logArr);
	};
	
	/**
	 * 获取用户信息
	 * @param roleId 角色ID
	 */
	private UserInfo getUserInfo(RoleAccount roleAccount){
		UserInfo userInfo = new UserInfo();  
		userInfo.putUid(roleAccount.getUid());  
		return userInfo;
	}
	
	/**
	 * 获取角色信息
	 * @param roleId 角色ID
	 */
	private RoleInfo getRoleInfo(RoleAccount roleAccount){
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.putServer(String.valueOf(roleAccount.getAreaId()));
		roleInfo.putRoleId(String.valueOf(roleAccount.getRoleId()));
		roleInfo.putRoleName(roleAccount.getRoleName());
		roleInfo.putRoleLevel(String.valueOf(roleAccount.getLevel()));
		roleInfo.putRoleVipLevel(String.valueOf(roleAccount.getVipLevel()));
		return roleInfo;
	}
	
	/**
	 * 添加日志数据到日志集合
	 * @param key 
	 * @param value
	 */
	protected void putLogData(String key,String value){
		dataMap.put(key, value);
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
}
