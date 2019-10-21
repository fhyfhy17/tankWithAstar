package com.ourpalm.tank.app.quest.domain;

/**
 * 登录任务阶段
 * @author wangkun
 *
 */
public class _1_RoleLoginPhase extends QuestPhase{

	public _1_RoleLoginPhase(int limit){
		super(limit);
	}
	
	@Override
	public boolean login() {
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
