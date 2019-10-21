package com.ourpalm.tank.vo.behaviortree;

import java.util.LinkedList;
import java.util.List;

public abstract class Node<E> implements Executable<E>, LifeCycle<E> {

	protected Node<E> parent;
	protected List<Node<E>> children;
	protected Status status;

	public Node() {
		this(new LinkedList<Node<E>>());
	}

	public Node(List<Node<E>> children) {
		this.children = children;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Node<E> getParent() {
		return parent;
	}

	public List<Node<E>> getChildren() {
		return children;
	}

	public Node<E> addChild(Node<E> node) {
		children.add(node);
		return this;
	}

	public Node<E> removeChild(int index) {
		children.remove(index);
		return this;
	}

	public int getChildCount() {
		return children.size();
	}

	public Node<E> getChildAt(int index) {
		return children.get(index);
	}

	public enum Status {

		RUNNING, SUCCESS, FAILURE;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status statue) {
		this.status = statue;
	}

	public boolean isRunning() {
		return status == Status.RUNNING;
	}

	public boolean isSuccess() {
		return status == Status.SUCCESS;
	}

	public boolean isFailure() {
		return status == Status.FAILURE;
	}

	@Override
	public void init(E receiver) {
		for (Node<E> node : children) {
			node.init(receiver);
		}
	}

	@Override
	public void reset(E receiver) {
		for (Node<E> child : children) {
			child.reset(receiver);
		}
		children.clear();
	}
}
