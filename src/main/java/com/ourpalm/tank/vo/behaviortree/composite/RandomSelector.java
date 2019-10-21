package com.ourpalm.tank.vo.behaviortree.composite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ourpalm.tank.vo.behaviortree.Node;

public class RandomSelector<E> extends Selector<E> {

	public RandomSelector() {
		super();
	}

	public RandomSelector(List<Node<E>> children) {
		super(children);
	}

	@SafeVarargs
	public RandomSelector(Node<E> ... children) {
		super(children);
	}

	@Override
	public boolean eval(E receiver) {
		if (children.isEmpty()) {
			return false;
		}

		Set<Integer> open = new HashSet<>();
		int i = 0;

		while (open.size() != children.size() && !open.contains(i)) {
			Node<E> child = getChildAt(i);
			if (child.eval(receiver)) {
				return true;
			}
			open.add(i);
			i = (int) (Math.random() * getChildCount());
		}

		return false;
	}
}
