package com.ourpalm.tank.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.message.ROLE_MSG.LOGIN_STATE;
import com.ourpalm.tank.vo.LoginCheckReq;
import com.ourpalm.tank.vo.LoginCheckResp;
import com.ourpalm.tank.vo.LoginUserInfo;
import com.ourpalm.tank.vo.result.LoginResult;
import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;

public abstract class AbstractLoginAction {
	protected Logger logger = LogCore.runtime;
	
	public static String GetResultFormatString( int nCode, String msg ) {
		return "{\"ret\":"+nCode+",\"msg\":\""+msg+"\"}"; 
	}
	
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
	
	protected LoginResult loginCheck(String openID, String openKey, String pf ){
		String serverPF = GameContext.getServerPf();
		if( serverPF.equals("TX") ) {
			return TxloginCheck( openID, openKey, pf );
		} else {
			return BaseloginCheck(openID);
		}
	}
	
	/** 登录验证逻辑 */
	protected LoginResult BaseloginCheck(String tokenId){
		LoginResult result = new LoginResult();
		
		LoginCheckReq req = new LoginCheckReq();
		req.setTokenId(tokenId);
		
		String reqMsg = "jsonStr="+JSON.toJSONString(req);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try{
			URL url = new URL(GameContext.getLoginCheckUrl());
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			
			if(logger.isDebugEnabled()){
				logger.debug("向用户中心登录验证, URL:{}, 请求 : {}", GameContext.getLoginCheckUrl(), reqMsg);
			}
			
			ByteArrayEntity entity = new ByteArrayEntity(reqMsg.getBytes());
			entity.setContentType("application/x-www-form-urlencoded");
			
			HttpPost httppost = new HttpPost(uri);
			httppost.setEntity(entity);
			
			CloseableHttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			int status = response.getStatusLine().getStatusCode();
			if(status < 200 || status >= 300){
				logger.error("httStatus=" + status + " targetUrl=" + GameContext.getLoginCheckUrl());
				result.setState(LOGIN_STATE.FAILURE);
				result.setInfo("参数错误");
				return result;
			}
			InputStream input = resEntity.getContent();
			byte[] data = new byte[input.available()];
			
			input.read(data);
			
			String respMsg = new String(data);
			
			if(logger.isDebugEnabled()){
				logger.debug("HTTP 返回: {}", respMsg);
			}
			
			if(Util.isEmpty(respMsg)){
				result.setState(LOGIN_STATE.FAILURE);
				result.setInfo("参数错误");
				return result;
			}
			
			LoginCheckResp resp = JSON.parseObject(respMsg, LoginCheckResp.class);
			if(!resp.getStatus().equals("0")){
				result.setState(LOGIN_STATE.FAILURE);
				result.setInfo(resp.getDesc());
				return result;
			}
			//获取用户ID
			LoginUserInfo userInfo = resp.getUserInfo();
			result.setUserInfo(userInfo);
			result.setState(LOGIN_STATE.SUCCESS);
		}catch(Exception e){
			result.setState(LOGIN_STATE.FAILURE);
			result.setInfo("参数错误");
			logger.error("", e);
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
			}
		}
		
		return result;
	}
	
	/**
	 * 测试调用UserInfo接口
	 *
	 */
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
	
	public static String TxGetTxUserInfo(OpenApiV3 sdk, String openid, String openkey, String pf)
	{
		// 指定OpenApi Cgi名字 
		String scriptName =GameContext.getTxQQUrl();

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
	
	protected LoginResult TxloginCheck( String openID, String openKey, String pf ){
		LoginResult result = new LoginResult();
		
		try{
			if( sdk == null ){
				initSDK();
			}
			
			// 目前腾讯因为免安装功能，不能调用 user_info 接口
//			String respQQInfo = "";
//			if( GameContext.getTxTestUserInfoOpen() == 1 ){
//				respQQInfo = GameContext.getTxTestQQUserInfo();
//			} else {
//				respQQInfo = TxGetTxUserInfo(sdk, openID, openKey, pf);
//				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
//				logger.info(" [PF_TEST] login get Tx QQ info OK, ret {} ", respQQInfo );
//				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
//			}
//			
//			String nickname = "";
//			int ret =  Integer.parseInt(JSONObject.parseObject(respQQInfo).getString("ret"));
//			if( ret == 0 ){
//				nickname = JSONObject.parseObject(respQQInfo).getString("nickname");
//			}
			
			String respBlueInfo = "";
			if( GameContext.getTxTestUserInfoOpen() == 1 ){
				if( GameContext.isPfVIP() ){
					respBlueInfo = GameContext.getTxTestBlueUserInfo();
				} else {
					respBlueInfo = GameContext.getTxTestNoBlueUserInfo();
				}
			} else {
				respBlueInfo = TxGetTxBlueInfo(sdk, openID, openKey, pf);
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
				logger.info(" [PF_TEST] login get Tx blue info OK, ret {} ", respBlueInfo );
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
			}
			
			String respYellowInfo = "";
			if( GameContext.getTxTestUserInfoOpen() == 1 ){
				if( GameContext.isPfVIP() ){
					respYellowInfo = GameContext.getTxTestYellowUserInfo();
				} else {
					respYellowInfo = GameContext.getTxTestNoYellowUserInfo();
				}
				
			} else {
				respYellowInfo = TxGetTxYellowInfo(sdk, openID, openKey, pf);
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
				logger.info(" [PF_TEST] login get Tx yellow info OK, ret {} ", respYellowInfo );
				logger.info(" [PF_TEST] -----------------------------------------------------------------------------------------------------" );
			}
			
			int retBlue =  Integer.parseInt(JSONObject.parseObject(respBlueInfo).getString("ret"));
			int retYellow =  Integer.parseInt(JSONObject.parseObject(respYellowInfo).getString("ret"));
			if( retBlue == 0 && retYellow == 0 ){
				//获取用户ID
				LoginUserInfo userInfo = new LoginUserInfo();
				PFUserInfo pfUserInfo = JSON.parseObject(respBlueInfo, PFUserInfo.class);
				PFYellowUserInfo pfYellowUserInfo = JSON.parseObject(respYellowInfo, PFYellowUserInfo.class);
				
				// 目前腾讯因为免安装功能，不能调用 user_info 接口
				//pfUserInfo.setNickname(nickname);
				
				userInfo.setPfUserInfo(pfUserInfo);
				userInfo.setPfYellowUserInfo(pfYellowUserInfo);
				
				userInfo.setId(openID);
				result.setUserInfo(userInfo);
				result.setState(LOGIN_STATE.SUCCESS);
				
				//logger.info(" [PF_TEST] login get Tx info OK PFUserInfo info {} ", JSON.toJSONString(pfUserInfo) );
			} else {
				//logger.info(" [PF_TEST] login get Tx blue info failed, ret {} ", respBlueInfo );
				//logger.info(" [PF_TEST] login get Tx yellow info failed, ret {} ", respYellowInfo );
				
				result.setState(LOGIN_STATE.FAILURE);
				if( retBlue == 1002 || retYellow == 1002 ){
					result.setInfo("您已经和服务器断开连接，请重启客户端！");
				} else {
					if( retBlue != 0 ){
						result.setInfo(JSONObject.parseObject(respBlueInfo).getString("msg"));
					} else {
						result.setInfo(JSONObject.parseObject(respYellowInfo).getString("msg"));
					}
				}
			}
		   
		}catch(Exception e){
			result.setState(LOGIN_STATE.FAILURE);
			result.setInfo("参数错误");
			logger.error("", e);
		}finally{

		}
		
		return result;
	}
	
}
