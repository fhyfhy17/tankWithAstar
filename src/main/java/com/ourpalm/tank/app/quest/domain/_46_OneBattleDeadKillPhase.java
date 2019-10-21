package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

/**
 * 一场对应战场死亡复仇同时炸死N个敌人（战场类型或关系）
 * @author Administrator
 *
 */
public class _46_OneBattleDeadKillPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	private int killCount;
	
	public _46_OneBattleDeadKillPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2)) {
			throw new IllegalArgumentException("创建任务 [一场对应战场死亡复仇同时炸死N个敌人 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
		killCount = Integer.parseInt(param2);
	}

	@Override
	public boolean oneBattleDeadKill(int warType, int killCount) {
		if(!this.warTypeSet.contains(warType)) {
			return false;
		}
		
		if(this.killCount > killCount) {
			return false;
		}
		
		this.progress += 1;
		
		return true;
	}
	@Override
	public boolean initProgress() {
		// TODO Auto-generated method stub
		return false;
	}

}
