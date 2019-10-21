package com.ourpalm.tank.app.quest.domain;

import java.util.Set;

/**
 * 获得mvp（或关系）
 * @author Administrator
 *
 */
public class _34_RoleMvpPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	
	public _34_RoleMvpPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务[获得mvp] 失败，参数错误	");
		}
		
		warTypeSet = splitParamToSet(param1);
	}

	@Override
	public boolean roleMvp(int warType) {
		if(!warTypeSet.contains(warType)) {
			return false;
		}
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		return false;
	}

}
