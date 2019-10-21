package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Chance<E> extends Decorator<E> {

	private float chance;

	public Chance(float chance, Node<E> node) {
		super(node);
		this.chance = chance;
	}

	@Override
	public boolean eval(E receiver) {
		if (Math.random() < chance) {
			return getChild().eval(receiver);
		}
		return false;
	}
}
