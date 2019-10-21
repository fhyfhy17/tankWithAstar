package com.ourpalm.tank.app.online;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.UserDAO;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.ROLE_MSG.LOGIN_STATE;
import com.ourpalm.tank.message.SERV_MSG.STG_OFFLINE_MSG;
import com.ourpalm.tank.message.SERV_MSG.STP_ROLE_LOGIN_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_LOGIN_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_OFFLINE_MSG;
import com.ourpalm.tank.vo.result.LoginResult;
import com.qq.open.OpenApiV3;
import com.qq.open.OpensnsException;

public class OnlineCenterImpl implements OnlineCenter {
	private final Logger logger = LogCore.runtime;
	private final static long HEARTBEAT_TIME_OUT_VAL = 5 * 60 * 1000; // 5分钟未收到心跳踢下线
	private final static long CHECK_TIME_VAL = 5 * 1000; // 5秒发送一次心跳并检测超时
	private final static long CHECK_OPEN_KEY_VAL = 60 * 60 * 1000; // 一小时续 openkey 时效
	
	// 腾讯发送续 open key 时效的服务器API
	private OpenApiV3 sdk = null;
	// 管理玩家在线连接 KEY为ioId
	private Map<Integer, RoleConnect> allConnectMap = new ConcurrentHashMap<>();
	// <roleId, ioId>
	private Map<Integer, Integer> roleToIoMap = new ConcurrentHashMap<>();

	private UserDAO userDAO;

	@Override
	public void start() {
		Thread task = new Thread(new Runnable() {
			@Override
			public void run() {
				initThread();
				while (true) {
					try {
						long startTime = System.currentTimeMillis();
						int count = 0;
						for (RoleConnect connect : allConnectMap.values()) {
							// 检测心跳超时
							if (checkHearbeat(connect)) {
								// 发送心跳信息
								connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_HEARTBEAT_VALUE, null);
								count += 1;
								checkOpenKey(connect);
							}
						}
						long finishTime = System.currentTimeMillis();
						long timeRunning = finishTime - startTime;
						if (timeRunning < CHECK_TIME_VAL) {
							Thread.sleep(CHECK_TIME_VAL - timeRunning);
						} else {
							Thread.sleep(1);
						}
						LogCore.system.info("当前在线人数为 : {}", count);
					} catch (Exception e) {
						LogCore.runtime.error("", e);
					}
				}
			}
		});
		task.start();
	}

	private boolean checkHearbeat(RoleConnect connect) {
		if ((System.currentTimeMillis() - connect.getHearbeatTime()) > HEARTBEAT_TIME_OUT_VAL) {

			STG_OFFLINE_MSG msg = STG_OFFLINE_MSG.newBuilder().setFromNode(GameContext.getLocalNodeName()).setIoId(connect.getIoId()).build();
			// 向网关推送下线消息
			connect.sendMsg(msg);

			if (logger.isDebugEnabled()) {
				logger.debug("心跳超时 ioId = {} roleId = {}", connect.getIoId(), connect.getRoleId());
			}

			// 下线处理
			offline(connect.getIoId(), true);

			return false;
		}
		return true;
	}
	
	private void initThread(){
		if(GameContext.isCheckLogin()){
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
	
	private void checkOpenKey(RoleConnect connect) {
		if ( sdk != null && (System.currentTimeMillis() - connect.getOpenKeyTime()) > CHECK_OPEN_KEY_VAL ) {
			connect.setOpenKeyTime(System.currentTimeMillis());
			
			// 指定OpenApi Cgi名字 
			String scriptName =GameContext.getTxIsLoginUrl();

			// 指定HTTP请求协议类型
			String protocol = "http";

			RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
			if( role != null ){
				// 填充URL请求参数
				HashMap<String,String> params = new HashMap<String, String>();
				params.put("openid", role.getUid());
				params.put("openkey", role.getOpenKey());
				params.put("pf", role.getPf());

				try {
					//logger.debug(" [PF_TEST] is login check openid {} openkey {} pf {} role id {}", role.getUid(), role.getOpenKey(), role.getPf(), connect.getRoleId() );
					sdk.api(scriptName, params, protocol);
				} catch (OpensnsException e) {
					//logger.debug(" [PF_TEST] is login check failed {} {}", e.getErrorCode(), e.getMessage() );
				}
			}			
		}
	}

	@Override
	public Collection<RoleConnect> getAllRoleConnect() {
		return this.allConnectMap.values();
	}

	@Override
	public void register(RoleConnect connect) {
		GameContext.getUserApp().saveRoleConnect(connect);
		this.allConnectMap.put(connect.getIoId(), connect);
		this.roleToIoMap.put(connect.getRoleId(), connect.getIoId());
	}

	@Override
	public LoginResult login(int areaId, String uid, int ioId) {
		LoginResult result = new LoginResult();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(areaId, uid);
		if (role == null) {
			result.setState(LOGIN_STATE.NO_ROLE);
			return result;
		}

		// 被封号
		if (role.hadLoginForbid()) {
			result.setState(LOGIN_STATE.FAILURE);
			result.setInfo("此账号已被封停");
			return result;
		}

		final int roleId = role.getRoleId();

		// 验证多点登录
		this.checkMultiLogin(roleId, ioId);

		// 回收下匹配IO，避免停服时残留的IO导致无法提交匹配
		GameContext.getMatchApp().removeIo(ioId);

		// 容错处理,退出组队
		try {
			GameContext.getMatchApp().teamQuit(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		result.setState(ROLE_MSG.LOGIN_STATE.SUCCESS);
		return result;
	}

	// 验证账号多点登录
	@Override
	public void checkMultiLogin(int roleId, int ioId) {
		RoleConnect connect = userDAO.getRoleConnect(roleId);
		if (connect == null) {
			return;
		}
		logger.debug("connect ioId = {}  ioId = {}", connect.getIoId(), ioId);
		// IO相同则不作处理，只是多发了一条登录消息而已
		// 否则导致登录被踢，登录不上去
		// IO全局唯一不用担心不同网关IO相同问题
		if (connect.getIoId() == ioId) {
			return;
		}
		// 如果链接存在，先把自身节点清理干净,再做下一步逻辑
		// 删除IO列表
		this.roleToIoMap.remove(roleId);
		// 删除链接对象
		this.allConnectMap.remove(connect.getIoId());
		// 下线逻辑
		// this.offlineByRoleId(connect.getRoleId(), true);
		this.offline(ioId, true);

		// 异地登录提示信息
		connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_MUTI_LOGIN_VALUE, null);

		// 踢另一端下线
		// connect.sendMsg(
		// STG_OFFLINE_MSG.newBuilder()
		// .setFromNode(GameContext.getLocalNodeName())
		// .setIoId(connect.getIoId())
		// .build());
	}

	@Override
	public void offline(int ioId, boolean offline) {
		RoleConnect connect = this.allConnectMap.remove(ioId);
		if (connect == null) {
			return;
		}

		final int roleId = connect.getRoleId();
		if (!roleToIoMap.containsKey(roleId) || roleToIoMap.get(roleId) != ioId) {
			return;
		}

		try {
			// 删除匹配记录
			GameContext.getMatchApp().removeIo(ioId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 直接调用该方法
		// 如果发送 STS_OFFLINE_MSG 协议的话，会因为上面的逻辑已经删除了 RoleAccount 对象，反而导致内部消息不能按 roleID 队列排队执行
		this.offlineByRoleId(roleId, offline);
		
//		STS_OFFLINE_MSG offlineMsg = STS_OFFLINE_MSG.newBuilder().setOffline(offline).setRoleId(roleId).build();
//
//		Message m = new Message();
//		m.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
//		m.setCmdId((byte) SERV_MSG.CMD_ID.STS_OFFLINE_VALUE);
//		m.setData(offlineMsg.toByteArray());
//		m.setFromNode(GameContext.getLocalNodeName());
//		m.setIoId(ioId);
//
//		GameContext.getMessageHandler().messageReceived(m);

		/**
		 * 
		 * //断线重连会导致，网关下线请求与玩家加载登录信息不在一个IO队列中执行 //非线程安全导致刚加载完的数据，又被网关通知下线释放掉了
		 * //缓存读写线程安全，所以从缓存中获取一份RoleConnect对象比较 RoleConnect cacheConnect =
		 * this.userDAO.getRoleConnect(roleId); if(cacheConnect == null){ return
		 * ; } //表示已经加载完成 //IO全局唯一，所以不需判断是否本地、异地登录 if(cacheConnect.getIoId() !=
		 * ioId){ return ; }
		 * 
		 * //删除IO列表 this.roleToIoMap.remove(roleId);
		 * 
		 * try{ //删除匹配记录 GameContext.getMatchApp().removeIo(ioId);
		 * }catch(Exception e){ logger.error("", e); }
		 * 
		 * //做退出组队处理 try{ if(offline){
		 * GameContext.getMatchApp().teamQuit(roleId); } }catch(Exception e){
		 * logger.error("", e); }
		 * 
		 * this.offlineByRoleId(roleId, offline);
		 **/

		// 判断是否本机登录，是则删除缓存记录,避免下次登录再次验证多点
		// 玩家跨服去别的服务器,IO是相同的,但已不在本服务器,此时如果把连接对象删除,同一个账号异地登录将失效
		// if(ioId == cacheConnect.getIoId()
		// &&
		// GameContext.getLocalNodeName().equals(cacheConnect.getNodeName())){
		// this.userDAO.removeRoleConnect(cacheConnect.getRoleId());
		// }
		// 下线删除链接对象
		/**
		 * if(offline){ this.userDAO.removeRoleConnect(roleId); }
		 **/
	}

	public void offlineByRoleId(int roleId, boolean offline) {

		this.roleToIoMap.remove(roleId);

		// 下线删除链接对象
		if (offline) {
			this.userDAO.removeRoleConnect(roleId);
		}

		// 做退出组队处理
		try {
			if (offline) {
				GameContext.getMatchApp().teamQuit(roleId);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		try {
			GameContext.getTankApp().offline(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		try {
			GameContext.getQuestApp().offline(roleId);
			GameContext.getMasterQuestApp().offline(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 删除缓存战斗记录
		try {
			GameContext.getMatchApp().removeLocalRoleBattle(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 删除成就缓存
		try {
			GameContext.getAchievementApp().offline(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 删除商城缓存
		try {
			GameContext.getShopApp().offline(roleId);
			GameContext.getHonorShopApp().offline(roleId);
			GameContext.getVipShopApp().offline(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 通知活动
		try {
			GameContext.getActivityApp().offline(roleId, offline);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 清除战斗信息缓存
		try {
			GameContext.getBattleApp().offline(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 清除缓存
		try {
			GameContext.getUserApp().offline(roleId, offline);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public boolean hadOnline(int roleId) {
		return this.roleToIoMap.containsKey(roleId);
	}

	@Override
	public RoleConnect getRoleConnect(int ioId) {
		return this.allConnectMap.get(ioId);
	}

	@Override
	public RoleConnect getRoleConnectByRoleId(int roleId) {
		if (roleToIoMap.containsKey(roleId)) {
			return this.getRoleConnect(roleToIoMap.get(roleId));
		}
		return null;
	}

	@Override
	public void stop() {
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public void sysLogin(RoleConnect connect, boolean hasLogin) {
		STS_LOGIN_MSG.Builder loginBuilder = STS_LOGIN_MSG.newBuilder();
		loginBuilder.setRoleId(connect.getRoleId());
		loginBuilder.setLogin(hasLogin);

		Message m = new Message();
		m.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
		m.setCmdId((byte) SERV_MSG.CMD_ID.STS_LOGIN_VALUE);
		m.setData(loginBuilder.build().toByteArray());
		m.setFromNode(GameContext.getLocalNodeName());
		m.setIoId(connect.getIoId());

		GameContext.getMessageHandler().messageReceived(m);
		// 向platForm服 发送登录信息

		RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
		if (remoteNode != null) {
			Message mplat = new Message();
			STP_ROLE_LOGIN_MSG.Builder platformLogonBuilder = STP_ROLE_LOGIN_MSG.newBuilder();
			platformLogonBuilder.setRoleId(connect.getRoleId());
			RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
			if (role != null) {
				platformLogonBuilder.setLevel(role.getLevel());
				platformLogonBuilder.setRoleName(role.getRoleName());
			}
			platformLogonBuilder.setAreaId(connect.getAreaId());
			platformLogonBuilder.setNodeName(connect.getNodeName());
			platformLogonBuilder.setIoId(connect.getIoId());

			mplat.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			mplat.setCmdId((byte) SERV_MSG.CMD_ID.STP_ROLE_LOGIN_VALUE);
			mplat.setData(platformLogonBuilder.build().toByteArray());
			mplat.setFromNode(GameContext.getLocalNodeName());

			remoteNode.sendReqMsg(mplat);
		}

	}
}
