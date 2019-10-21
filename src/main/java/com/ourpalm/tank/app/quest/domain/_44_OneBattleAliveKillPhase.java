package com.ourpalm.tank.app.quest.domain;

import java.util.Set;
/**
 * 一场对应战场整场战斗不死，击杀N个敌人（战场类型或关系）
 * @author Administrator
 *
 */
public class _44_OneBattleAliveKillPhase extends QuestPhase {

	private Set<Integer> warTypeSet;
	private int killCount;
	
	public _44_OneBattleAliveKillPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2)) {
			throw new IllegalArgumentException("创建任务 [一场对应战场整场战斗不死，击杀N个敌人 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
		killCount = Integer.parseInt(param2);
	}

	@Override
	public boolean oneBattleAliveKill(int warType, int killCount) {
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
		return false;
	}

}
