package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 使用指定类型坦克完成比赛
 * 
 * @author wangkun
 *
 */
public class CampUseTypeTankStar implements CampStar{

	private int type;		//坦克类型
	
	public CampUseTypeTankStar(int type){
		this.type = type;
	}
	
	@Override
	public boolean condition(AbstractInstance tank) {
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		if(template == null){
			return false;
		}
		return template.getTankType_i() == type;
	}

}
