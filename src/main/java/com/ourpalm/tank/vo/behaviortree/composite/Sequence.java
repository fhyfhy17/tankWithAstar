package com.ourpalm.tank.vo.behaviortree.composite;

import java.util.List;

import com.ourpalm.tank.vo.behaviortree.Composite;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Sequence<E> extends Composite<E> {

	public Sequence() {
		super();
	}

	public Sequence(List<Node<E>> children) {
		super(children);
	}

	@SafeVarargs
	public Sequence(Node<E> ... children) {
		super(children);
	}

	@Override
	public boolean eval(E receiver) {
		for (Node<E> child : getChildren()) {
			if (!child.eval(receiver)) {
				return false;
			}
		}
		return true;
	}
}
