package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

/**
 * 战斗累计获取N个荣誉点
 * @author Administrator
 *
 */
public class _41_TotalBattleHonorPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	
	public _41_TotalBattleHonorPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务 {战斗累计获取N个荣誉点] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
	}
	
	@Override
	public boolean totalBattleHonor(int warType, int honor) {
		if(!warTypeSet.contains(warType)) {
			return false;
		}
		
		this.progress += honor;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
