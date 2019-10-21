package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.app.map.state.BeginStateMachine;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public class BattleTimePassedCondition extends MapNode {

	Operator operator = Operator.EQUAL;
	int progress;

	public BattleTimePassedCondition(MapInstance map, Operator operator, int progress) {
		super(map);
		this.operator = operator;
		this.progress = progress;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		StateMachine sm = map.getStateMachine();
		if (sm instanceof BeginStateMachine) {
			BeginStateMachine bsm = (BeginStateMachine) sm;
			int time = bsm.getTotalTime();
			long btime = bsm.getBeginTime();
			int progress = (int) ((System.currentTimeMillis() - btime) * 100.0 / time);
			return operator.eval(progress, this.progress);
		}
		return false;
	}

	public static Node<AbstractInstance> E(MapInstance map, int progress) {
		return new BattleTimePassedCondition(map, Operator.EQUAL, progress);
	}

	public static Node<AbstractInstance> G(MapInstance map, int progress) {
		return new BattleTimePassedCondition(map, Operator.GREATER, progress);
	}

	public static Node<AbstractInstance> GE(MapInstance map, int progress) {
		return new BattleTimePassedCondition(map, Operator.GREATER_EQUAL, progress);
	}

	public static Node<AbstractInstance> L(MapInstance map, int progress) {
		return new BattleTimePassedCondition(map, Operator.LESS, progress);
	}

	public static Node<AbstractInstance> LE(MapInstance map, int progress) {
		return new BattleTimePassedCondition(map, Operator.LESS_EQUAL, progress);
	}
}
