package com.ourpalm.tank.app.quest.domain;

public class _33_TotalKillTankPhase extends QuestPhase {

	public _33_TotalKillTankPhase(int limit) {
		super(limit);
	}

	@Override
	public boolean totalKillTank(int count) {
		this.progress += count;
		return true;
	}
	
	@Override
	public boolean initProgress() {
		return false;
	}

}
