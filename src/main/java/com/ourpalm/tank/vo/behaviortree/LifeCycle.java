package com.ourpalm.tank.vo.behaviortree;

public interface LifeCycle<E> {

	void init(E receriver);

	void reset(E receiver);
}
