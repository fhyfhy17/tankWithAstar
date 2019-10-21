package com.ourpalm.core.action;

public interface ActionFactory<T> {

	Action<T> getAction(int cmdType, int cmdId);

}
