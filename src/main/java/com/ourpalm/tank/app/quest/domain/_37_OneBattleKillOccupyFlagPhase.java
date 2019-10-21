package com.ourpalm.tank.app.quest.domain;

/**
 * 一场战斗击杀N个正在夺旗的敌人
 * @author Administrator
 *
 */
public class _37_OneBattleKillOccupyFlagPhase extends QuestPhase {

	private int killCount;
	
	public _37_OneBattleKillOccupyFlagPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务 [一场战斗击杀N个正在夺旗的敌人] 失败，参数错误");
		}
		
		this.killCount = Integer.parseInt(param1);
	}
	
	@Override
	public boolean oneBattleKillOcccpyFlag(int killCount) {
		if(this.killCount > killCount) {
			return false;
		}
		
		this.progress += 1;
		return true;
	}


	@Override
	public boolean initProgress() {
		return false;
	}

}
