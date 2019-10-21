package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.util.RandomUtil;

public class TankAiTemplate {
	private String aiTank;		//AI坦克
	private int min;			//匹配分最小值
	private int max;			//匹配分最大值
	private int battleScoreMin; //战斗力最小值
	private int battleScoreMax; //战斗力最大值

	
	private List<Integer> tankList = new ArrayList<>();
	
	public void init(){
		if(Util.isEmpty(aiTank)){
			LogCore.startup.warn("坦克配置表中,没有配置任何AI坦克列表");
			return ;
		}
		this.buildTankList(aiTank, tankList);
		
		if(max <= min){
			LogCore.startup.error("坦克配置表 AI坦克中 min={} max={} 战斗力最大值必须大于最小值", min, max);
		}
	}
	
	
	private void buildTankList(String ids, List<Integer> tankList){
		for(String id : ids.split(Cat.comma)){
			if(Util.isEmpty(id)){
				continue;
			}
			int tankId = Integer.parseInt(id);
			TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
			if(template == null){
				LogCore.startup.error("坦克配置表 AI坦克中 min={} max={} tankId={}  坦克不存在", min, max, tankId);
				continue;
			}
			tankList.add(tankId);
		}
	}
	
	/** 随机战斗力 */
	public int randomBattleScore(){
		return RandomUtil.randomInt(battleScoreMin, battleScoreMax);
	}
	

	public List<Integer> getTankList() {
		return tankList;
	}
	
	public String getAiTank() {
		return aiTank;
	}

	public void setAiTank(String aiTank) {
		this.aiTank = aiTank;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}


	public int getBattleScoreMin() {
		return battleScoreMin;
	}


	public void setBattleScoreMin(int battleScoreMin) {
		this.battleScoreMin = battleScoreMin;
	}


	public int getBattleScoreMax() {
		return battleScoreMax;
	}


	public void setBattleScoreMax(int battleScoreMax) {
		this.battleScoreMax = battleScoreMax;
	}
}
