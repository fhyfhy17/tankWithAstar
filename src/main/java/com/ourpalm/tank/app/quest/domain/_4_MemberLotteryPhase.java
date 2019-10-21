package com.ourpalm.tank.app.quest.domain;

/**
 * 进行N次武将抽取
 * @author Administrator
 *
 */
public class _4_MemberLotteryPhase extends QuestPhase {
	
	public _4_MemberLotteryPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean memberLottery(int count) {
		this.progress += count;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
