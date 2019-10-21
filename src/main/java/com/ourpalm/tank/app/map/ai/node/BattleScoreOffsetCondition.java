package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

/**
 * 条件节点：战斗比分我方减敌方的差值判断
 */
public class BattleScoreOffsetCondition extends MapNode {

	Operator operator = Operator.EQUAL;
	int offset;

	public BattleScoreOffsetCondition(MapInstance map, Operator operator, int offset) {
		super(map);
		this.operator = operator;
		this.offset = offset;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		int o = map.getRedKillNum() - map.getBlueKillNum();
		return operator.eval(
			receiver.getTeam() == TEAM.RED ? o : -o,
			offset);
	}

	public static Node<AbstractInstance> E(MapInstance map, int offset) {
		return new BattleScoreOffsetCondition(map, Operator.EQUAL, offset);
	}

	public static Node<AbstractInstance> G(MapInstance map, int offset) {
		return new BattleScoreOffsetCondition(map, Operator.GREATER, offset);
	}

	public static Node<AbstractInstance> GE(MapInstance map, int offset) {
		return new BattleScoreOffsetCondition(map, Operator.GREATER_EQUAL, offset);
	}

	public static Node<AbstractInstance> L(MapInstance map, int offset) {
		return new BattleScoreOffsetCondition(map, Operator.LESS, offset);
	}

	public static Node<AbstractInstance> LE(MapInstance map, int offset) {
		return new BattleScoreOffsetCondition(map, Operator.LESS_EQUAL, offset);
	}
}
