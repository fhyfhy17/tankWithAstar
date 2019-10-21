package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.SeasonMatch;

public interface SeasonMatchDao {

	SeasonMatch get();
	
	void save(SeasonMatch seasonMatch);
}
