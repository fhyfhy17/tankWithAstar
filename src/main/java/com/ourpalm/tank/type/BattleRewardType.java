package com.ourpalm.tank.type;

public enum BattleRewardType {

	iron_rws(1), //银币
	tank_exp_rws(2), //坦克经验
	honor_rws(3),	//荣誉
	;
	
	public int type;
	
	BattleRewardType(int type){
		this.type = type;
	}
	
}
