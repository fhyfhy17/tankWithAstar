package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.behaviortree.DataContext;
import com.ourpalm.tank.vo.behaviortree.Node;

public class SetSearchStrategyAction extends Node<AbstractInstance> {

	SearchStrategy strategy;

	public SetSearchStrategyAction(SearchStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		receiver.getRoadManager().setStrategy(strategy);
		receiver.getRoadManager().clearRoads();
		if (strategy.disableAttack()) {
			DataContext dc = receiver.getBahaviorDataContext();
			dc.putBoolean(FireAction.FIRE_MODE, false);
			receiver.setFireTarget(null);
		}
		if (strategy != SearchStrategy.DEFAULT) {
			LogCore.runtime.info("{} 更改为 {} 移动策略", receiver.getRoleName(), strategy);
		}
		return true;
	}

}
