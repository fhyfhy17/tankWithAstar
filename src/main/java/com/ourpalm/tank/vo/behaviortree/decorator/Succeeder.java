package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Succeeder<E> extends Decorator<E> {

	public Succeeder(Node<E> node) {
		super(node);
	}

	@Override
	public boolean eval(E receiver) {
		getChild().eval(receiver);
		return true;
	}
}
