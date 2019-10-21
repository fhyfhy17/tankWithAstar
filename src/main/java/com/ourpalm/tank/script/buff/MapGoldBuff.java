package com.ourpalm.tank.script.buff;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.buff.BuffApp;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_DROP_CLEAR_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_DROP_DRAW_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.MapInstance;

/**
 * 坦克死亡随机掉落金币
 * 
 * @author wangkun
 *
 */
public class MapGoldBuff implements IBuff{
	//随机半径
	private static final int radius = 5;
	
	private int id;						//buff实例id
	private float x;					//产出位置x
	private float z;					//产出位置z
	private int gold;					//金币数
	private MapInstance mapInstance;	
	
	public MapGoldBuff(){
		this.id = BuffApp.idFactory.incrementAndGet();
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void startup() {
		
	}

	@Override
	public void update() {
		float shortRange = radius;
		AbstractInstance obtainTank = null;
		for(AbstractInstance tank : mapInstance.getAllTank()){
			float range = Util.range(tank.getX(), tank.getZ(), x, z);
			if(range > radius || tank.isDeath()){
				continue;
			}
			
			if(range < shortRange){
				shortRange = range;
				obtainTank = tank;
			}
		}
		
		if(obtainTank == null){
			return ;
		}
		
		//给获得者金币
		if(obtainTank.isRobot()){
			this.clear();
			return ;
		}
		
		int roleId = obtainTank.getRoleId();
		GameContext.getUserAttrApp().changeAttribute(roleId, 
				AttrUnit.build(RoleAttr.gold, Operation.add, gold), OutputType.battlefieldDropRewardInc );
		//推送奖励提示
		STC_DROP_DRAW_MSG drawMsg = STC_DROP_DRAW_MSG.newBuilder()
				.setMoneyType(RoleAttr.gold_VALUE)
				.setCount(gold)
				.build();
		obtainTank.sendMsg(drawMsg);
		
		this.clear();
	}

	@Override
	public void resetTime() {
		
	}

	@Override
	public int getTemplateId() {
		return 0;
	}

	@Override
	public void clear() {
		mapInstance.remove(id);
		
		//给所有人发送金币消失
		STC_DROP_CLEAR_MSG clearMsg = STC_DROP_CLEAR_MSG.newBuilder()
				.setId(id)
				.build();
		mapInstance.brodcastMsg(clearMsg);
	}

	@Override
	public int getEffectId() {
		return 0;
	}

	@Override
	public int getTime() {
		return 0;
	}


	public void setX(float x) {
		this.x = x;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setMapInstance(MapInstance mapInstance) {
		this.mapInstance = mapInstance;
	}
}
