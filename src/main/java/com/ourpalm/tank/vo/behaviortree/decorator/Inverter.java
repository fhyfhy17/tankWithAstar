package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Inverter<E> extends Decorator<E> {

	public Inverter(Node<E> node) {
		super(node);
	}

	@Override
	public boolean eval(E receiver) {
		return !getChild().eval(receiver);
	}
}
