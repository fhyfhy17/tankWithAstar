package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.dblog.HandleDBLog;

public class RoleLogModule{
	
	private final int LOG_ID = 3000;
	
	enum RoleDBLogType {

		upLevel(300001, "角色等级升级"),	
		rename(300002, "角色改名"),	
		;
		
		private int type;
		private String info;
		
		RoleDBLogType(int type, String info){
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
	
	public void roleUpLevel( int roleId, int oldLevel, int level ){
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.upLevel.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, oldLevel);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, level);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
		
	}
	
	public void roleRename( int roleId, String oldName, String newName ){
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.upLevel.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, oldName);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, newName);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
		
	}
	
}
