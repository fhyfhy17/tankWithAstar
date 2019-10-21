package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.DataContext;

public class EnemyDetectedCondition extends MapNode {

	public static final String DETECT_LOOP = "EnemyDetectedCondition.detectLoop";

	enum ViewRange {

		OWN {
			@Override
			boolean view(AbstractInstance object, AbstractInstance subject) {
				return object.viewSelf(subject);
			}
		}, // 自己视野

		SHARED {
			@Override
			boolean view(AbstractInstance object, AbstractInstance subject) {
				return object.view(subject);
			}
		}, // 共享视野

		SAFE {
			@Override
			boolean view(AbstractInstance object, AbstractInstance subject) {
				// Object 能看见 Subject 但是 Subject 看不见 Object
				return object.view(subject) && !subject.view(object);
			}
		}; // 安全视野

		abstract boolean view(AbstractInstance object, AbstractInstance subject);
	}

	ViewRange viewRange = ViewRange.SHARED;

	public EnemyDetectedCondition(MapInstance map, ViewRange viewRange) {
		super(map);
		this.viewRange = viewRange;
	}

	@Override
	public void init(AbstractInstance receiver) {
		super.init(receiver);
		DataContext dc = receiver.getBahaviorDataContext();
		dc.putBean(DETECT_LOOP, new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME));
	}

	@Override
	public void reset(AbstractInstance tank) {
		DataContext dc = tank.getBahaviorDataContext();
		dc.remove(DETECT_LOOP);
		super.reset(tank);
	}

	@Override
	public boolean eval(AbstractInstance receiver) {

		AbstractInstance target = receiver.getFireTarget();
		if (target != null && receiver.getRoadManager().getStrategy() == SearchStrategy.PURSUE) {
			return true;
		}

		LoopCount loopCount = receiver.getBahaviorDataContext().getBean(DETECT_LOOP, LoopCount.class);
		if (loopCount != null && !loopCount.isReachCycle()) {
			return false;
		}

		float minRange = Integer.MAX_VALUE;
		AbstractInstance nearest = null;
		DataContext dc = receiver.getBahaviorDataContext();

		// 寻找离自身最近的目标
		for(AbstractInstance tank : map.getAllTank()) {

			if (tank.isDeath()
					|| tank.getId() == receiver.getId()
					|| tank.getTeam() == receiver.getTeam()) {
				continue;
			}

			float range = Util.range(receiver.getX(), receiver.getZ(), tank.getX(), tank.getZ());
			if (range < minRange) {
				minRange = range;
				nearest = tank;
			}
		}

		// 判断射程
		if (receiver.get(AttrType.range) < minRange) {
			return false;
		}

		// 判断视野
		if (!viewRange.view(receiver, nearest)) {
			return false;
		}

		receiver.setFireTarget(nearest);
		dc.putBoolean(FireAction.FIRE_MODE, true);

		return true;
	}
}
