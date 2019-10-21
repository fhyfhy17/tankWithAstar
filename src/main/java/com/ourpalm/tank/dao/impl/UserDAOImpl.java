package com.ourpalm.tank.dao.impl;

import java.util.HashMap;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.UserDAO;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;

public class UserDAOImpl extends AbstractJedisDao implements UserDAO{
	private final static String ROLE_ACCOUNT_KEY = "ROLE_ACCOUNT_";  //存储账号KEY
	private final static String ROLE_CONNECT_KEY = "ROLE_CONNECT_";	 //玩家连接KEY
	private final static String ROLE_CORPS_ID_KEY = "ROLE_CORPS_ID_"; //玩家军团ID
	private final static String ROLE_PF_USER_INFO_KEY = "PF_USER_INFO_"; //平台蓝钻信息	
	private final static String ROLE_PF_YELLOW_USER_INFO_KEY = "PF_YELLOW_USER_INFO_"; //平台黄钻信息	
	private final static int ROLE_CONNECT_EXPIRE = 30 * 60; //连接有效时限半个小时
	protected Logger logger = LogCore.runtime;
	// 腾讯服务器API
	private OpenApiV3 sdk = null;
	
	private void initSDK(){
		if(sdk == null && GameContext.isCheckLogin()){
			// OpenAPI的服务器IP 
			// 最新的API服务器地址请参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3 
			String serverName = GameContext.getTxloginCheckUrl();
	
			// 所要访问的平台, pf的其他取值参考wiki文档: http://wiki.open.qq.com/wiki/API3.0%E6%96%87%E6%A1%A3
	
			String appid = GameContext.getTxAppId();
			String appkey = GameContext.getTxAppKey();
			
			sdk = new OpenApiV3(appid, appkey);
			sdk.setServerName(serverName);
		}
	}
	
	public static String GetResultFormatString( int nCode, String msg ) {
		return "{\"ret\":"+nCode+",\"msg\":\""+msg+"\"}"; 
	}
	
	public static String TxGetTxBlueInfo(OpenApiV3 sdk, String openid, String openkey, String pf)
	{
		// 指定OpenApi Cgi名字 
		String scriptName =GameContext.getTxBlueUrl();

		// 指定HTTP请求协议类型
		String protocol = "http";

		// 填充URL请求参数
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("openid", openid);
		params.put("openkey", openkey);
		params.put("pf", pf);

		try
		{
			return sdk.api(scriptName, params, protocol);
		}
		catch (OpensnsException e)
		{
			//System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
			//e.printStackTrace();
			return GetResultFormatString(e.getErrorCode(), e.getMessage());
		}
	}
	
	public static String TxGetTxYellowInfo(OpenApiV3 sdk, String openid, String openkey, String pf)
	{
		// 指定OpenApi Cgi名字 
		String scriptName =GameContext.getTxYellowUrl();

		// 指定HTTP请求协议类型
		String protocol = "http";

		// 填充URL请求参数
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("openid", openid);
		params.put("openkey", openkey);
		params.put("pf", pf);

		try
		{
			return sdk.api(scriptName, params, protocol);
		}
		catch (OpensnsException e)
		{
			//System.out.printf("Request Failed. code:%d, msg:%s\n", e.getErrorCode(), e.getMessage());
			//e.printStackTrace();
			return GetResultFormatString(e.getErrorCode(), e.getMessage());
		}
	}
	
	@Override
	public RoleAccount getRoleAccount(int areaId, String uid) {
		//账号_分区 为唯一KEY
		String key = uid + "_" + areaId;
		if(!getClient().exists(key)){
			return null;
		}
		int roleId = Integer.parseInt(getClient().get(key));
		return this.getRoleAccount(roleId);
	}

	@Override
	public RoleAccount getRoleAccount(int roleId) {
		String acStr = getClient().get(ROLE_ACCOUNT_KEY + roleId);
		return JSON.parseObject(acStr, RoleAccount.class);
	}

	
	@Override
	public void createRoleAccount(RoleAccount account){
		//账号_分区 为唯一KEY
		String uid = account.getUid();
		int areaId = account.getAreaId();
		String key = uid + "_" + areaId;
		//保存账号信息
		getClient().set(key, String.valueOf(account.getRoleId()));
		
		//建立角色名称与角色ID对应关系
		this.addNameRoleIdLink(account.getRoleName(), account.getRoleId());
		
		this.saveRoleAccount(account);
	}
	
	@Override
	public void delNameRoleIdLink(String roleName){
		client.del(roleName);
	}
	
	@Override
	public void addNameRoleIdLink(String roleName, int roleId){
		client.set(roleName, roleId+"");
	}
	
	@Override
	public int getRoleId(String roleName){
		String strRoleId = client.get(roleName);
		if(Util.isEmpty(strRoleId)){
			return -1;
		}
		return Integer.parseInt(strRoleId);
	}
	
	@Override
	public void saveRoleAccount(RoleAccount account) {
		getClient().set(ROLE_ACCOUNT_KEY + account.getRoleId(), JSON.toJSONString(account));
	}

	
	@Override
	public RoleConnect getRoleConnect(int roleId) {
		String conStr = getClient().get(ROLE_CONNECT_KEY + roleId);
		return JSON.parseObject(conStr, RoleConnect.class);
	}

	
	@Override
	public void saveRoleConnect(RoleConnect roleConnect) {
		String key = ROLE_CONNECT_KEY + roleConnect.getRoleId();
		client.set(key, JSON.toJSONString(roleConnect));
		client.expire(key, ROLE_CONNECT_EXPIRE);
	}

	@Override
	public void removeRoleConnect(int roleId) {
		getClient().del(ROLE_CONNECT_KEY + roleId);
	}
	
	
	@Override
	public boolean roleNameHadExist(String roleName){
		return client.exists(roleName);
	}

	@Override
	public void saveCorpsId(int roleId, int corpsId) {
		client.set(ROLE_CORPS_ID_KEY + roleId, String.valueOf(corpsId));
	}

	@Override
	public int getCorpsId(int roleId) {
		String strCorpsId = client.get(ROLE_CORPS_ID_KEY + roleId);
		if(Util.isEmpty(strCorpsId)){
			return 0;
		}
		return Integer.parseInt(strCorpsId);
	}
	
	@Override
	public PFUserInfo getPfUserInfo(int roleId) {
		String acStr = getClient().get(ROLE_PF_USER_INFO_KEY + roleId);
		return JSON.parseObject(acStr, PFUserInfo.class);
	}
	
	@Override
	public PFUserInfo getPfUserUpdateInfo(int roleId) {
		PFUserInfo pfUserInfo = null;
		try{
			if( sdk == null ){
				initSDK();
			}
			
			String respBlueInfo = "";
			if( GameContext.getTxTestUserInfoOpen() == 1 ){
				if( GameContext.isPfVIP() ){
					respBlueInfo = GameContext.getTxTestBlueUserInfo();
				} else {
					respBlueInfo = GameContext.getTxTestNoBlueUserInfo();
				}
			} else {
				RoleAccount role = this.getRoleAccount(roleId);
				String openID = role.getUid();
				String openKey = role.getOpenKey();
				String pf = role.getPf();
				respBlueInfo = TxGetTxBlueInfo(sdk, openID, openKey, pf);
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
				logger.info(" [PF_TEST] update get Tx blue info OK, ret {} ", respBlueInfo );
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
			}
			
			int ret = Integer.parseInt(JSONObject.parseObject(respBlueInfo).getString("ret"));
			if( ret == 0 ){
				pfUserInfo = JSON.parseObject(respBlueInfo, PFUserInfo.class);

				getClient().set(ROLE_PF_USER_INFO_KEY + roleId, JSON.toJSONString(pfUserInfo));
				return pfUserInfo;
			} else {
				return this.getPfUserInfo(roleId);
			}
		   
		}catch(Exception e){
			return this.getPfUserInfo(roleId);
		}
	}
	
	@Override
	public void savePfUserInfo(int roleId, PFUserInfo pfUserInfo) {
		getClient().set(ROLE_PF_USER_INFO_KEY + roleId, JSON.toJSONString(pfUserInfo));
	}
	
	@Override
	public PFYellowUserInfo getPfYellowUserInfo(int roleId) {
		String acStr = getClient().get(ROLE_PF_YELLOW_USER_INFO_KEY + roleId);
		return JSON.parseObject(acStr, PFYellowUserInfo.class);
	}
	
	@Override
	public PFYellowUserInfo getPfYellowUserUpdateInfo(int roleId) {
		PFYellowUserInfo pfUserInfo = null;
		try{
			if( sdk == null ){
				initSDK();
			}
			
			String respYellowInfo = "";
			if( GameContext.getTxTestUserInfoOpen() == 1 ){
				if( GameContext.isPfVIP() ){
					respYellowInfo = GameContext.getTxTestYellowUserInfo();
				} else {
					respYellowInfo = GameContext.getTxTestNoYellowUserInfo();
				}
			} else {
				RoleAccount role = this.getRoleAccount(roleId);
				String openID = role.getUid();
				String openKey = role.getOpenKey();
				String pf = role.getPf();
				respYellowInfo = TxGetTxYellowInfo(sdk, openID, openKey, pf);
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
				logger.info(" [PF_TEST] update get Tx yellow info OK, ret {} ", respYellowInfo );
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
			}
			
			int ret = Integer.parseInt(JSONObject.parseObject(respYellowInfo).getString("ret"));
			if( ret == 0 ){
				pfUserInfo = JSON.parseObject(respYellowInfo, PFYellowUserInfo.class);

				getClient().set(ROLE_PF_YELLOW_USER_INFO_KEY + roleId, JSON.toJSONString(pfUserInfo));
				return pfUserInfo;
			} else {
				return this.getPfYellowUserInfo(roleId);
			}

		}catch(Exception e){
			return this.getPfYellowUserInfo(roleId);
		}
	}
	
	@Override
	public void savePfYellowUserInfo(int roleId, PFYellowUserInfo pfYellowUserInfo) {
		getClient().set(ROLE_PF_YELLOW_USER_INFO_KEY + roleId, JSON.toJSONString(pfYellowUserInfo));
	}
}
