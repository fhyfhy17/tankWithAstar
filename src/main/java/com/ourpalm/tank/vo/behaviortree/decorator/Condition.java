package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Condition<E> extends Decorator<E> {

	private Node<E> condition;

	public Condition(Node<E> child, Node<E> condition) {
		super(child);
		this.condition = condition;
	}

	@Override
	public boolean eval(E receiver) {
		Node<E> node = getChild();
		if (condition.eval(receiver)) {
			return node.eval(receiver);
		}
		return false;
	}
}
