package com.ourpalm.tank.app.quest.domain;

import java.util.Set;
/**
 * 一场对应战场整场战斗不死，获得N个助攻 （或关系）
 * 
 * @author Administrator
 *
 */
public class _42_OneBattleAliveHelpPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	private int helpCount;

	public _42_OneBattleAliveHelpPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2) ) {
			throw new IllegalArgumentException("创建任务 [一场对应战场整场战斗不死，获得N个助攻 ] 失败，参数错误");
		}
		
		warTypeSet = splitParamToSet(param1);
		helpCount = Integer.parseInt(param2);
	}

	@Override
	public boolean oneBattleAliveHelp(int warType, int helpCount) {
		if(!warTypeSet.contains(warType)) {
			return false;
		}
		if(this.helpCount > helpCount) {
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
