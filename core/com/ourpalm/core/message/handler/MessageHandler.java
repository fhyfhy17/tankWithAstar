package com.ourpalm.core.message.handler;

import com.ourpalm.tank.message.Message;

public interface MessageHandler {
	
	void startup();

	void messageReceived(Message msg);
	
}
