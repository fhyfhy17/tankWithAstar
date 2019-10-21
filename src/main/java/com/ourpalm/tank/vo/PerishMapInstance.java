package com.ourpalm.tank.vo;

import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.template.MapTemplate;


/**
 * 歼灭战地图实例
 * 
 * @author wangkun
 *
 */
public class PerishMapInstance extends BattleMapInstance{

	public PerishMapInstance(MapTemplate template, int instanceId, int warType) {
		super(template, instanceId, warType);
		
		this.blueKillNum = template.getPlayerCount();
		this.redKillNum = template.getPlayerCount();
	}

	
	
	/** 统计战场比分 */
	@Override
	protected void calcuWarScore(AbstractInstance killer, AbstractInstance target){
		//统计队伍击杀数
		if(TEAM.BLUE == target.getTeam()){
			this.blueKillNum = Math.max(0, this.blueKillNum - 1);
		}else{
			this.redKillNum = Math.max(0, this.redKillNum - 1);
		}
	}
}
