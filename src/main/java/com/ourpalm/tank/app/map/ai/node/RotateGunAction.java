package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.app.map.RoadManager;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class RotateGunAction extends MapNode {

	public RotateGunAction(MapInstance map) {
		super(map);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {

		AbstractInstance target = receiver.getFireTarget();
		RoadManager rm = receiver.getRoadManager();

		Grid grid = receiver.getRoadManager().getAStarGrid();

		com.ourpalm.tank.app.map.astar.Node selfNode = grid.getNodeByWoldPoint(receiver.getX(), receiver.getZ());
		selfNode.setTankOccupy(false);

		receiver.setX(rm.getX());
		receiver.setY(rm.getY());
		receiver.setZ(rm.getZ());

		selfNode = grid.getNodeByWoldPoint(receiver.getX(), receiver.getZ());
		selfNode.setTankOccupy(true);

		// 把炮台转向目标
		receiver.syncCoordinate(target);

		// 给转炮塔留时间
//		receiver.setLastFireTime(System.currentTimeMillis());

		// 从 ReloadShell 复制过来，开始填弹
		if (rm.nextNode() != null) {
			rm.nextNode().setTankOccupy(false);
		}

		return true;
	}
}
