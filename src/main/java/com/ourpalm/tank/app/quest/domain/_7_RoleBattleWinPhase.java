package com.ourpalm.tank.app.quest.domain;

import java.util.Set;
/**
 * 获得N场对应战场胜利(或关系）
 * @author Administrator
 *
 */
public class _7_RoleBattleWinPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	
	public _7_RoleBattleWinPhase(int limit, String param1) {
		super(limit);
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建[获得N场对应战场胜利]任务失败，参数错误");
		}
		warTypeSet = splitParamToSet(param1);
	}
	
	
	@Override
	public boolean roleBattleWin(int warType) {
		if(!this.warTypeSet.contains(warType)) {
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
