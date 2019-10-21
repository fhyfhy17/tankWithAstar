package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class ReachNextNodeCondition extends MapNode {


	public ReachNextNodeCondition(MapInstance map) {
		super(map);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		return receiver.getRoadManager().reachNextNode();
	}
}
