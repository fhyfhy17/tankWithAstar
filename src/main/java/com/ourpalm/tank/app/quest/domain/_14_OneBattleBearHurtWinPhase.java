package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

/**
 * 一场战斗战场累计承受伤害超过N%，且本方获胜
 * @author Administrator
 *
 */
public class _14_OneBattleBearHurtWinPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	private int hurtPercent;

	public _14_OneBattleBearHurtWinPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2) ) {
			throw new IllegalArgumentException("创建任务 [一场战斗战场累计承受伤害超过N%，且本方获胜 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
		hurtPercent = Integer.parseInt(param2);
	}

	@Override
	public boolean initProgress() {
		return false;
	}


	@Override
	public boolean oneBattleBearHurtPercentWin(int warType, int hurtPercent) {
		if(!warTypeSet.contains(warType)) {
			return false;
		}
		
		if(hurtPercent < this.hurtPercent) {
			return false;
		}
		
		this.progress += 1;
		
		return true;
	}

}
