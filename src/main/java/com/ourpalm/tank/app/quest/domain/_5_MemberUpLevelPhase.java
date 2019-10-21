
package com.ourpalm.tank.app.quest.domain;

/**
 *	成员升级 
 *
 */
public class _5_MemberUpLevelPhase extends QuestPhase {
	
	public _5_MemberUpLevelPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean memberUpLevel() {
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
