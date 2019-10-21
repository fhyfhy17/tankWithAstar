package com.ourpalm.tank.vo.behaviortree;

public interface Executable<E> {

	boolean eval(E receiver);
}
