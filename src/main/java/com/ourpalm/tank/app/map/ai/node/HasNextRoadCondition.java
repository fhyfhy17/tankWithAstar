package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public class HasNextRoadCondition extends Node<AbstractInstance> {

	@Override
	public boolean eval(AbstractInstance receiver) {
		return !receiver.getRoadManager().roadsEmpty();
	}
}
