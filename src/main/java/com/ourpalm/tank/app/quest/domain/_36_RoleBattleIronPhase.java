package com.ourpalm.tank.app.quest.domain;


/**
 * 战场获得银币 
 *
 */
public class _36_RoleBattleIronPhase extends QuestPhase{

	public _36_RoleBattleIronPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean roleBattleIron(int iron) {
		this.progress += iron;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
