package com.ourpalm.tank.app.dblog.module;

import com.ourpalm.tank.app.dblog.DBLogAppImpl;

public class GameDBLogApp extends DBLogAppImpl {
	
	private UserLogModule userLog = new UserLogModule();
	private RoleLogModule roleLog = new RoleLogModule();
	private MoneyLogModule moneyLog = new MoneyLogModule();
	private QuestAchievementLogModule questLog = new QuestAchievementLogModule();
	private BattleLogModule battleLog = new BattleLogModule();
	
	public UserLogModule getUserLog() {
		return userLog;
	}
	public void setUserLog(UserLogModule userLog) {
		this.userLog = userLog;
	}
	public RoleLogModule getRoleLog() {
		return roleLog;
	}
	public void setRoleLog(RoleLogModule roleLog) {
		this.roleLog = roleLog;
	}
	public MoneyLogModule getMoneyLog() {
		return moneyLog;
	}
	public void setMoneyLog(MoneyLogModule moneyLog) {
		this.moneyLog = moneyLog;
	}
	public QuestAchievementLogModule getQuestLog() {
		return questLog;
	}
	public void setQuestLog(QuestAchievementLogModule questLog) {
		this.questLog = questLog;
	}
	public BattleLogModule getBattleLog() {
		return battleLog;
	}
	public void setBattleLog(BattleLogModule battleLog) {
		this.battleLog = battleLog;
	}

}
