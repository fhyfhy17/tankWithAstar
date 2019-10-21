package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public class LevelCondition extends Node<AbstractInstance> {

	Operator operator = Operator.EQUAL;

	LevelCondition(Operator operator) {
		this.operator = operator;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {

		AbstractInstance target = receiver.getFireTarget();
		if (target == null) {
			return false;
		}
		return operator.eval(receiver.getLevel(), target.getLevel());
	}

	public static Node<AbstractInstance> E() {
		return new LevelCondition(Operator.EQUAL);
	}

	public static Node<AbstractInstance> G() {
		return new LevelCondition(Operator.GREATER);
	}

	public static Node<AbstractInstance> GE() {
		return new LevelCondition(Operator.GREATER_EQUAL);
	}

	public static Node<AbstractInstance> L() {
		return new LevelCondition(Operator.LESS);
	}

	public static Node<AbstractInstance> LE() {
		return new LevelCondition(Operator.LESS_EQUAL);
	}
}
