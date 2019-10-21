package com.ourpalm.tank.vo.behaviortree.composite;

import java.util.List;

import com.ourpalm.tank.vo.behaviortree.Composite;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Parallel<E> extends Composite<E> {

	public Parallel() {
		super();
	}

	public Parallel(List<Node<E>> children) {
		super(children);
	}

	@SafeVarargs
	public Parallel(Node<E> ... children) {
		super(children);
	}

	@Override
	public boolean eval(E receiver) {
		for (Node<E> child : getChildren()) {
			child.eval(receiver);
		}
		return true;
	}
}
