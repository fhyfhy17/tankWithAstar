package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.DataContext;

public class PursueAvailableCondition extends MapNode {

	private static final int MAX_WAIT_TIME_COUNT = 3;

	public PursueAvailableCondition(MapInstance map) {
		super(map);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {

		DataContext dc = receiver.getBahaviorDataContext();
		AbstractInstance target = receiver.getFireTarget();

		boolean canPursue = canPursue(receiver, target);

		if (!canPursue) {
			int waitTimeCount = dc.getInt(FireAction.WAIT_TIME_COUNT);
			waitTimeCount += 1;
			if (waitTimeCount >= MAX_WAIT_TIME_COUNT) {
				waitTimeCount = 0;
				receiver.setFireTarget(null);
				target.setAttacker(null);
				receiver.getRoadManager().setStrategy(SearchStrategy.DEFAULT);
				dc.putBoolean(FireAction.FIRE_MODE, false);
			}
			dc.putInt(FireAction.WAIT_TIME_COUNT, waitTimeCount);
			return false;
		}

		boolean canAttack = canAttack(receiver, target);
		boolean cdOver = receiver.hadAndSetFireCoolTime();
		return canAttack && cdOver;
	}

	private boolean canPursue(AbstractInstance self, AbstractInstance target) {
		return !self.isDeath()
				&& target != null
				&& !target.isDeath();
	}

	private boolean canAttack(AbstractInstance self, AbstractInstance target) {
		return !self.isDeath()
				&& target != null
				&& !target.isDeath()
				&& self.turrentTowardsWith(target)
				&& self.view(target) // 是否进入视野
				&& self.fireRange(target) // 进入射程
				&& self.isFireElevation(target) // 俯仰角判断
				&& !grid.hadBarrier(self.getX(), self.getZ(), target.getX(), target.getZ()); // 是否有阻挡
	}
}
