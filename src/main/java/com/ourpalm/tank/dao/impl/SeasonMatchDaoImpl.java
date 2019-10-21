package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.SeasonMatchDao;
import com.ourpalm.tank.domain.SeasonMatch;

public class SeasonMatchDaoImpl extends AbstractJedisDao implements SeasonMatchDao{

	private final static String SEASON_KEY = "SEASON_KEY";	//赛季信息KEY
	
	
	@Override
	public SeasonMatch get(){
		String jsonStr = this.client.get(SEASON_KEY);
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, SeasonMatch.class);
	}
	
	
	@Override
	public void save(SeasonMatch seasonMatch){
		this.client.set(SEASON_KEY, JSON.toJSONString(seasonMatch));
	}
}
