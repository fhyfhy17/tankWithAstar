package com.ourpalm.tank.app.quest.domain;
/**
 * 进行N场战斗
 * @author Administrator
 *
 */
public class _32_RoleBattlePhase extends QuestPhase {
	
	public _32_RoleBattlePhase(int limit){
		super(limit);
	}
	
	@Override
	public boolean roleBattle() {
		this.progress += 1;
		return true;
	}
	
	@Override
	public boolean initProgress() {
		return false;
	}

}
