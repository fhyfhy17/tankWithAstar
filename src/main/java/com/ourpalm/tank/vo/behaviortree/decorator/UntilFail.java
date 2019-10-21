package com.ourpalm.tank.vo.behaviortree.decorator;

import com.ourpalm.tank.vo.behaviortree.Decorator;
import com.ourpalm.tank.vo.behaviortree.Node;

public class UntilFail<E> extends Decorator<E> {

	public UntilFail(Node<E> node) {
		super(node);
	}

	@Override
	public boolean eval(E receiver) {
		while (getChild().eval(receiver)) {}
		return true;
	}
}
