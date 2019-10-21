package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class MoveAction extends MapNode {

	public MoveAction(MapInstance map) {
		super(map);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		receiver.getRoadManager().walk();
		return true;
	}
}
