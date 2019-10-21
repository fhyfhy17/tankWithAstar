package com.ourpalm.tank.app.quest.domain;

import com.ourpalm.tank.app.GameContext;

public class _35_RoleUpLevelPhase extends QuestPhase {
	
	public _35_RoleUpLevelPhase(int limit) {
		super(limit);
	}

	@Override
	public boolean roleUpLevel(int curLevel) {
		this.progress = curLevel;
		return true;
	}

	@Override
	public boolean initProgress() {
		this.progress = GameContext.getUserApp().getRoleAccount(roleId).getLevel();
		return true;
	}

}
