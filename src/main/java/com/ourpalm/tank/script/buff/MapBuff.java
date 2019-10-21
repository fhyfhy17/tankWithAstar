package com.ourpalm.tank.script.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_MINE_BOMB_MSG;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

public class MapBuff extends Buff{
	
	private MapInstance mapInstance;
	private float x ;
	private float z ;
	private int radius; 	//触发半径
	
	@Override
	public void startup() {
		this.x = source.getX();
		this.z = source.getZ();
	}
	

	@Override
	public void update() {
		List<AbstractInstance> hitTanks = new ArrayList<>();
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.isDeath() || tank.getTeam() == source.getTeam()){
				continue;
			}
			
			float x = Math.abs(tank.getX() - this.x);
			float z = Math.abs(tank.getZ() - this.z);
			
			int range = (int)Math.sqrt(x*x  + z*z);
			if(radius > range){
				hitTanks.add(tank);
			}
		}
		
		if(Util.isEmpty(hitTanks)){
			return ;
		}
		//删除buff
		this.removeBuff();
		
		//结算伤害
		for(AbstractInstance tank : hitTanks){
			if(Util.isEmpty(attrMap)){
				continue;
			}
			for(Entry<AttrType, Float> entry : attrMap.entrySet()){
				AttrType attrType = entry.getKey();
				Float value = tank.get(attrType);
				if(value == null){
					continue;
				}
				value += entry.getValue();
				LogCore.runtime.debug("buff影响属性 {} : {}", attrType, value);
				tank.changeAttr(attrType, value);
			}
			//同步属性变化
			tank.synchChangeAttr();
			
			if(tank.isDeath()){
				mapInstance.death(source, tank);
			}
		}
	}

	private void removeBuff(){
		IBuff buff = mapInstance.remove(id);
		if(buff == null){
			return ;
		}
		STC_MINE_BOMB_MSG msg = STC_MINE_BOMB_MSG.newBuilder()
				.setId(id)
				.setGoodsId(effectId)
				.build();
		mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_MINE_BOMB_VALUE, msg.toByteArray());
	}

	
	@Override
	public void clear() {
		this.removeBuff();
	}

	public void setMapInstance(MapInstance mapInstance) {
		this.mapInstance = mapInstance;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
}
