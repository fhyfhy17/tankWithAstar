package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.dblog.HandleDBLog;

public class BattleLogModule{
	
	private final int LOG_ID = 3002;
	
	enum RoleDBLogType {

		reportCount(300201, "战场报告-次数"),
		reportValue(300202, "战场报告-数量"),
		robotReportCount(300203, "机器人战场报告-次数"),
		robotReportValue(300204, "机器人战场报告-数量"),
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
	
	public void reportCount( int roleId, 
			int instanceId, String team, int tankId, int killCount, int helpKillCount, 
			int deathCount, int missCount, int dodgeCount, int findCount,int win){
		
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.reportCount.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, "F"+instanceId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, team);
		log.getRecord().set(HandleDBLog.LOG_CHAR3_KEY, tankId);
		log.getRecord().set(HandleDBLog.LOG_CHAR4_KEY, killCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR5_KEY, helpKillCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR6_KEY, deathCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR7_KEY, missCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR8_KEY, dodgeCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR9_KEY, findCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR10_KEY, win);
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}	
	
	public void reportValue( int roleId, 
			int instanceId, String team, int tankId, float allDamage, float allAidDamage, float allReceiveDamage){
		
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.reportValue.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, "F"+instanceId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, team);
		log.getRecord().set(HandleDBLog.LOG_CHAR3_KEY, tankId);
		log.getRecord().set(HandleDBLog.LOG_CHAR4_KEY, allDamage);
		log.getRecord().set(HandleDBLog.LOG_CHAR5_KEY, allAidDamage);
		log.getRecord().set(HandleDBLog.LOG_CHAR6_KEY, allReceiveDamage);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}	
	
	public void robotReportCount( int roleId, 
			int instanceId, String team, int tankId, int killCount, int helpKillCount, 
			int deathCount, int missCount, int dodgeCount, int findCount){
		
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.robotReportCount.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, "F"+instanceId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, team);
		log.getRecord().set(HandleDBLog.LOG_CHAR3_KEY, tankId);
		log.getRecord().set(HandleDBLog.LOG_CHAR4_KEY, killCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR5_KEY, helpKillCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR6_KEY, deathCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR7_KEY, missCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR8_KEY, dodgeCount);
		log.getRecord().set(HandleDBLog.LOG_CHAR9_KEY, findCount);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}	
	
	public void robotReportValue( int roleId, 
			int instanceId, String team, int tankId, float allDamage, float allAidDamage, float allReceiveDamage){
		
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.robotReportValue.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, "F"+instanceId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, team);
		log.getRecord().set(HandleDBLog.LOG_CHAR3_KEY, tankId);
		log.getRecord().set(HandleDBLog.LOG_CHAR4_KEY, allDamage);
		log.getRecord().set(HandleDBLog.LOG_CHAR5_KEY, allAidDamage);
		log.getRecord().set(HandleDBLog.LOG_CHAR6_KEY, allReceiveDamage);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}
}
