
package com.ourpalm.tank.app.quest.domain;

import java.util.Set;
/**
 * 占旗胜利(或关系）
 * @author Administrator
 *
 */
public class _11_RoleOccupyFlagWinPhase extends QuestPhase {
	private Set<Integer> warTypeSet;
	
	public _11_RoleOccupyFlagWinPhase(int limit, String param1) {
		super(limit);
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建[占旗胜利]任务失败，参数错误");
		}
		warTypeSet = splitParamToSet(param1);
	}

	@Override
	public boolean occupyFlagWin(int warType) {
		if(!this.warTypeSet.contains(warType)) {
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
