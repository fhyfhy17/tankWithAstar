package com.ourpalm.tank.vo.behaviortree.composite;

import java.util.List;

import com.ourpalm.tank.vo.behaviortree.Composite;
import com.ourpalm.tank.vo.behaviortree.Node;

public class Selector<E> extends Composite<E> {

	public Selector() {
		super();
	}

	public Selector(List<Node<E>> children) {
		super(children);
	}

	@SafeVarargs
	public Selector(Node<E> ... children) {
		super(children);
	}

	@Override
	public boolean eval(E receiver) {
		for (Node<E> child : getChildren()) {
			if (child.eval(receiver)) {
				return true;
			}
		}
		return false;
	}
}
