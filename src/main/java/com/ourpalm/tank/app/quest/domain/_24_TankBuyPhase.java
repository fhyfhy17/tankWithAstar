package com.ourpalm.tank.app.quest.domain;

import java.util.Collection;
import java.util.Set;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleTank;

/**
 * 坦克研发（或关系）
 * @author Administrator
 *
 */
public class _24_TankBuyPhase extends QuestPhase {
	private Set<Integer> tankIdSet;

	public _24_TankBuyPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建{坦克研发] 任务失败，参数错误");
		}
		
		tankIdSet = splitParamToSet(param1);
	}
	
	@Override
	public boolean tankBuy(int tankId) {
		if(!tankIdSet.contains(tankId)) {
			return false;
		}
		
		this.progress += 1;
		return true;
	}

	@Override
	public boolean initProgress() {
		boolean t = false;
		for(Integer _tankId : tankIdSet) {
			Collection<RoleTank> tankList = GameContext.getTankApp().getAllRoleTank(roleId);
			for(RoleTank tank : tankList) {
				if(tank.getTankId() == _tankId) {
					this.progress += 1;
					t = true;
				}
			}
			
		}
		
		return t;
	}

}
