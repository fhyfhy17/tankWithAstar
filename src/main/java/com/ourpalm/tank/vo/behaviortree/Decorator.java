package com.ourpalm.tank.vo.behaviortree;

import java.util.Collections;

public abstract class Decorator<E> extends Node<E> {

	public Decorator(Node<E> node) {
		children = Collections.<Node<E>>singletonList(node);
	}

	public Node<E> getChild() {
		return children.get(0);
	}
}
