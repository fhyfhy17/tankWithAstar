package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.util.RandomUtil;

public class UserLevelTemplate {

	private int level;
	private int exp;
	private int rewardIronMax;				//主战场比赛结束奖励银币最大值
	private String matchMaps;				//匹配地图
	
	private List<Integer> randomMapList = new ArrayList<>();

	
	public void init(){
		if(Util.isEmpty(matchMaps)){
			LogCore.startup.error("matchMaps = {} 没有配置地图信息");
			return ;
		}
		String[] mapStrs = matchMaps.split(Cat.comma);
		for(String mapIndex : mapStrs){
			if(Util.isEmpty(mapIndex)){
				continue;
			}
			int id = Integer.parseInt(mapIndex);
			MapTemplate mapTemplate = GameContext.getMapApp().getMapTemplate(id);
			if(mapTemplate == null){
				LogCore.startup.error("mapIndex = {} 此地图不存在", id);
				continue;
			}
			this.randomMapList.add(id);
		}
	}
	
	/** 随机地图索引 */
	public int randomMapIndex(){
		int index = RandomUtil.randomInt(randomMapList.size());
		return randomMapList.get(index);
	}
	
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getRewardIronMax() {
		return rewardIronMax;
	}

	public void setRewardIronMax(int rewardIronMax) {
		this.rewardIronMax = rewardIronMax;
	}

	public String getMatchMaps() {
		return matchMaps;
	}

	public void setMatchMaps(String matchMaps) {
		this.matchMaps = matchMaps;
	}
}
