package com.ourpalm.tank.app.buff;

import java.util.concurrent.atomic.AtomicInteger;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.template.BuffTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

public interface BuffApp extends Service{
	
	AtomicInteger idFactory = new AtomicInteger(1); 

	void putBuff(AbstractInstance source, AbstractInstance target, int buffId);
	
	BuffTemplate getBuffTemplate(int buffId);
	
	void remove(AbstractInstance target, int buffId);
}
