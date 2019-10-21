package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

/**
 * 一场战斗对就战场协助伤害超过N%且获得胜利(或关系）
 * @author Administrator
 *
 */
public class _48_OneBattleHelpHurtWinPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	private int hurtPercent;

	public _48_OneBattleHelpHurtWinPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2) ) {
			throw new IllegalArgumentException("创建任务 [一场战斗对就战场协助伤害超过N%且获得胜利 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
		hurtPercent = Integer.parseInt(param2);
	}

	@Override
	public boolean initProgress() {
		return false;
	}


	@Override
	public boolean oneBattleHelpHurtPercentWin(int warType, int percent) {
		if(!warTypeSet.contains(warType)) {
			return false;
		}
		
		if(percent < this.hurtPercent) {
			return false;
		}
		
		this.progress += 1;
		return true;
	}


}
