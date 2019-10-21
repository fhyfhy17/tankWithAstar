package com.ourpalm.tank.vo;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_DROP_LOCATION_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_TANK_DIE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.script.buff.MapGoldBuff;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.util.RandomUtil;

/**
 * 对战模式地图实例
 * 
 * @author wangkun
 *
 */
public class BattleMapInstance extends SportMapInstance{
	
	private int allGold;			//随机产出金币总数
	private int goldRat;			//金币产出概率
	private int randomGoldLimit;	//金币每次产出上限

	public BattleMapInstance(MapTemplate template, int instanceId, int warType) {
		super(template, instanceId, warType);
		this.allGold = template.getGold();
		this.goldRat = template.getGoldRat();
		this.randomGoldLimit = template.getRandomGoldLimit();
	}

	/** 广播死亡消息 */
	@Override
	public void deathNotifyMsg(AbstractInstance killer, AbstractInstance target){
		int freeReviveCount = 0;
		if(!target.isRobot()){
			int roleId = target.getRoleId();
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			freeReviveCount = roleWarInfo.getFreeReviveCount();
		}
		
		//广播死亡消息
		STC_TANK_DIE_MSG.Builder deathMsgBuilder = STC_TANK_DIE_MSG.newBuilder();
		deathMsgBuilder.setId(target.getId());
		deathMsgBuilder.setReliveTime(template.getReviveTime());
		deathMsgBuilder.setGold(template.getReviveGold());
		deathMsgBuilder.setHasRelive(true);
		deathMsgBuilder.setPayRelive(true); //是否可花钱复活
		deathMsgBuilder.setFreeCount(freeReviveCount);//免费复活次数
		deathMsgBuilder.setAtkName(killer.getRoleName());
		deathMsgBuilder.setAtkTankId(killer.getTemplateId());
		deathMsgBuilder.setAtkId(killer.getId());
		
		this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_TANK_DIE_VALUE, 
				deathMsgBuilder.build().toByteArray());
	}
	
	
	@Override
	public void death(AbstractInstance tank){
		super.death(tank);
		
		//随机产出金币
		if(allGold <= 0){
			return ;
		}
		
		//判断触发几率
		if(RandomUtil.randomInt(100) > this.goldRat){
			return ;
		}
		
		randomGoldLimit = randomGoldLimit > 1 ? randomGoldLimit : 2;
		int gold = RandomUtil.randomInt(1, randomGoldLimit);
		if(gold > allGold){
			gold = allGold;
		}
		
		MapGoldBuff buff = new MapGoldBuff();
		buff.setMapInstance(this);
		buff.setGold(gold);
		buff.setX(tank.getX());
		buff.setZ(tank.getZ());
		
		this.putBuff(buff);
		
		STC_DROP_LOCATION_MSG dropMsg = STC_DROP_LOCATION_MSG.newBuilder()
				.setId(buff.getId())
				.setMoneyType(RoleAttr.gold_VALUE)
				.setX(tank.getX())
				.setY(tank.getY())
				.setZ(tank.getZ())
				.build();
		this.brodcastMsg(dropMsg);
		
		allGold -= gold;
	}
}
