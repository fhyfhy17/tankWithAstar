package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Semaphore<E> extends Decorator<E> {

	public Semaphore(Node<E> node) {
		super(node);
	}

	@Override
	public boolean eval(E receiver) {
		return new Inverter<>(new Succeeder<>(getChild())).eval(receiver);
	}
}
