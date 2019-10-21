package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.app.map.RoadManager;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public class MoveStrategyCondition extends Node<AbstractInstance> {

	RoadManager.SearchStrategy strategy;

	public MoveStrategyCondition(RoadManager.SearchStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		RoadManager.SearchStrategy s = receiver.getRoadManager().getStrategy();
		return s == strategy;
	}
}
