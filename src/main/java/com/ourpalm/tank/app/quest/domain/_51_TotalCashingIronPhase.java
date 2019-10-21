package com.ourpalm.tank.app.quest.domain;

/**
 * 通过兑换累计获取N银币
 * @author Administrator
 *
 */
public class _51_TotalCashingIronPhase extends QuestPhase {

	public _51_TotalCashingIronPhase(int limit) {
		super(limit);
	}

	@Override
	public boolean initProgress() {
		return false;
	}


	@Override
	public boolean totalCashingIron(int iron) {
		this.progress += iron;
		return true;
	}


}
