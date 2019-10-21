package com.ourpalm.tank.vo.behaviortree;

import java.util.LinkedList;
import java.util.List;

public class BehaviorTree<E> implements Executable<E> {

	private Node<E> root;
	private List<Node<E>> managedNodes;

	public BehaviorTree(Node<E> root) {
		this.root = root;
		managedNodes = new LinkedList<>();
		managedNodes.add(root);
	}

	@Override
	public boolean eval(E receiver) {
		if (root != null) {
			return root.eval(receiver);
		}
		return false;
	}

	public void initialize(E receiver) {
		root.init(receiver);
	}

	public void clearChildren(E receiver) {
		root.reset(receiver);
		managedNodes.clear();
	}
}
