package com.ourpalm.tank.app.quest.domain;

import java.util.Set;
/**
 * 在对应战场击杀N辆坦克（或关系）
 * @author Administrator
 *
 */
public class _8_RoleBattleKillTankPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	
	public _8_RoleBattleKillTankPhase(int limit, String param1) {
		super(limit);
		if(isInvalid(param1) ) {
			throw new IllegalArgumentException("创建[获得N场对应战场胜利]任务失败，参数错误");
		}
		warTypeSet = splitParamToSet(param1);
	}

	@Override
	public boolean roleBattleKillTank(int warType, int killCount) {
		if(!this.warTypeSet.contains(warType)) {
			return false;
		}
		
		this.progress += killCount;
		
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
