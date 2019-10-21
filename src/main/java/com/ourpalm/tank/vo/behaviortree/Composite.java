package com.ourpalm.tank.vo.behaviortree;

import java.util.List;

public abstract class Composite<E> extends Node<E> {

	public Composite() {
		super();
	}

	public Composite(List<Node<E>> children) {
		super(children);
	}

	@SafeVarargs
	public Composite(Node<E> ... children) {
		for (Node<E> child : children) {
			addChild(child);
		}
	}
}
