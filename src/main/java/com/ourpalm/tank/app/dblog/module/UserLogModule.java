package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.dblog.UserDBLog;

public class UserLogModule{
	
	private final int LOG_ID = 1000;
	
	enum UserDBLogType {

		registerInc(100001, "注册"),
		loginInc(100002, "登录"),						
		logoutInc(100003, "退出"),		 					
		;
		
		private int type;
		private String info;
		
		UserDBLogType(int type, String info){
			this.type = type;
			this.info = info;
		}
		
		public int type(){
			return this.type;
		}
		
		public String getInfo(){
			return this.info;
		}
	}
	
	public void userRegister( String userId, int roleId, int serverId, String roleName, String pf, long createTime, String ip ){
		UserDBLog log = new UserDBLog();
		log.initLog(roleId, this.LOG_ID, UserDBLogType.registerInc.type());
		
		log.getRecord().set("user_id", userId);
		log.getRecord().set("role_id", roleId);
		log.getRecord().set("server_id", serverId);
		log.getRecord().set("name", roleName);
		log.getRecord().set("pf", pf);
		log.getRecord().set("level", 1);
		log.getRecord().set("create_time", createTime);
		log.getRecord().set("online_time", 0);
		log.getRecord().set("ip", ip);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
		
	}
	
	public void userLogin( String userId, int roleId, int serverId, String roleName, int roleLevel, String pf, String ip ){
		UserDBLog log = new UserDBLog();
		log.initLog(roleId, this.LOG_ID, UserDBLogType.loginInc.type());
		
		log.getRecord().set("user_id", userId);
		log.getRecord().set("role_id", roleId);
		log.getRecord().set("server_id", serverId);
		log.getRecord().set("name", roleName);
		log.getRecord().set("level", roleLevel);
		log.getRecord().set("pf", pf);
		log.getRecord().set("create_time", 0);
		log.getRecord().set("online_time", 0);
		log.getRecord().set("ip", ip);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}
	
	public void userLogout( String userId, int roleId, int serverId, String roleName, int roleLevel, String pf, String ip, int onlineTime ){
		UserDBLog log = new UserDBLog();
		log.initLog(roleId, this.LOG_ID, UserDBLogType.logoutInc.type());
		
		log.getRecord().set("user_id", userId);
		log.getRecord().set("role_id", roleId);
		log.getRecord().set("server_id", serverId);
		log.getRecord().set("name", roleName);
		log.getRecord().set("level", roleLevel);
		log.getRecord().set("pf", pf);
		log.getRecord().set("create_time", 0);
		log.getRecord().set("online_time", onlineTime);
		log.getRecord().set("ip", ip);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}
}
