package com.ourpalm.tank.app.quest.domain;
/**
 * 升级N次普通配件
 * @author Administrator
 *
 */
public class _3_TankUpPartPhase extends QuestPhase {
	
	public _3_TankUpPartPhase(int limit) {
		super(limit);
	}
	
	@Override
	public boolean tankUpPart() {
		this.progress += 1;
		return true;
	}
	
	@Override
	public boolean initProgress() {
		return false;
	}

}
