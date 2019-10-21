package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

public class _56_TeamBattlePhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	public _56_TeamBattlePhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务 [与好友组队进行战斗 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
	}

	@Override
	public boolean initProgress() {
		return false;
	}
	
	@Override
	public boolean teamBattle(int warType) {
		if(!warTypeSet.contains(warType))
			return false;
		
		this.progress += 1;
		
		return true;
	}

}
