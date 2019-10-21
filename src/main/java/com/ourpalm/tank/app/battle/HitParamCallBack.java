package com.ourpalm.tank.app.battle;

import com.ourpalm.tank.vo.AbstractInstance;

public interface HitParamCallBack {
	void call(AbstractInstance self, AbstractInstance target);
}
