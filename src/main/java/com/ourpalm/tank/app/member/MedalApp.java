package com.ourpalm.tank.app.member;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.vo.result.MedalCompositeResult;

public interface MedalApp extends Service {
	
	/**
	 * 合成
	 * @param roleId
	 * @param medalId
	 * @param count
	 * @return
	 */
	MedalCompositeResult composite(int roleId, int medalId, int count);
	
}
