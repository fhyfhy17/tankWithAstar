package com.ourpalm.tank.app.quest.domain;

public class _28_ShopBuyPhase extends QuestPhase {
	public _28_ShopBuyPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean shopBuy() {
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
