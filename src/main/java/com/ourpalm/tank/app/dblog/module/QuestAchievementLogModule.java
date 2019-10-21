package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.dblog.HandleDBLog;

public class QuestAchievementLogModule{
	
	private final int LOG_ID = 3001;
	
	enum RoleDBLogType {

		questFinish(300101, "主线、支线任务完成"),	
		activeQuestFinish(300102, "活跃任务完成"),	
		achievementFinish(300103, "成就完成"),	
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
	
	public void questFinish( int roleId, int questId, String questName ){
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.questFinish.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, questId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, questName);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}
	
	public void activeQuestFinish( int roleId, int questId, String questName ){
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.activeQuestFinish.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, questId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, questName);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
	}
	
	public void achievementFinish( int roleId, int achievementId, String achievementName ){
		HandleDBLog log = new HandleDBLog();
		log.initLog(roleId, this.LOG_ID, RoleDBLogType.achievementFinish.type());
		
		log.getRecord().set(HandleDBLog.LOG_CHAR1_KEY, achievementId);
		log.getRecord().set(HandleDBLog.LOG_CHAR2_KEY, achievementName);
		
		GameContext.getGameDBLogApp().offerLogToQueue(log);
		
	}	
}
