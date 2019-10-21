package com.ourpalm.tank.app.map.ai;

import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 死亡AI
 * 
 * @author hzh
 *
 */
public class DeathAi extends Ai {

	public DeathAi(AbstractInstance self) {
		super(self);
	}

	@Override
	public void init() {
		super.init();
		this.teamId = self.getTeam().getNumber();
	}
	
	@Override
	public void update() {
	}

	@Override
	public AiType getType() {
		// TODO Auto-generated method stub
		return AiType.death;
	}

}
