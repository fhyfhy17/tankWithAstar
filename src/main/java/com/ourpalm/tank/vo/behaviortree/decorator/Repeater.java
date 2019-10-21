package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Repeater<E> extends Decorator<E> {

	private int loopCount;

	public Repeater(Node<E> node) {
		this(node, 1);
	}

	public Repeater(Node<E> node, int loopCount) {
		super(node);
		this.loopCount = loopCount;
	}

	@Override
	public boolean eval(E receiver) {
		for (int i = 0; i < loopCount; i++) {
			getChild().eval(receiver);
		}
		return true;
	}
}
