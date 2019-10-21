package com.ourpalm.tank.script.buff;

public interface IBuff {
	
	int getId();
	
	void startup();
	
	void update();
	
	void resetTime();
	
	int getTemplateId();
	
	void clear();
	
	int getEffectId();
	
	int getTime();
}
