
package com.ourpalm.tank.app.quest.domain;

/**
 *	开宝箱完成任务 
 */
public class _6_BoxOpenPhase extends QuestPhase {
	
	public _6_BoxOpenPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean boxOpen() {
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
