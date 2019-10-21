package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.DataContext;

public class PursueAction extends MapNode {

	public static final String TARGET_TANK = "PursueAction.targetTank";

	public PursueAction(MapInstance map) {
		super(map);
	}


	@Override
	public void reset(AbstractInstance tank) {
		DataContext dc = tank.getBahaviorDataContext();
		dc.remove(TARGET_TANK);
		super.reset(tank);
	}


	@Override
	public boolean eval(AbstractInstance receiver) {

		return false;
	}
}
