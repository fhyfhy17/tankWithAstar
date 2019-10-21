package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class SearchRoardAction extends MapNode {

	public SearchRoardAction(MapInstance map) {
		super(map);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		if (!GameContext.isUseAiPath()) {
			receiver.getRoadManager().searchRoad();
		} else {
			receiver.getRoadManager().searchRoadEx();
		}
		SearchStrategy strategy = receiver.getRoadManager().getStrategy();
		if (strategy != SearchStrategy.DEFAULT) {
			LogCore.runtime.info("{} 移动策略 {} 下寻路", receiver.getRoleName(), strategy);
		}
		return true;
	}
}
