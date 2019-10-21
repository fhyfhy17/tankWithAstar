package com.ourpalm.tank.app.map.state;


public interface StateMachine {
	
	void update();
	
	void notify(NotifyState state, int tankInstanceId);
	
	boolean hadOver();

	void forceClose();
}
