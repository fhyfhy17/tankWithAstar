package com.ourpalm.tank.script.buff;

import java.util.Map.Entry;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;


/**
 * buff时间结束时，触发爆炸效果
 * 
 * @author wangkun
 *
 */
public class TimeBombBuff extends Buff{

	private int radius;
	
	@Override
	public void startup() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void update() {
		if ((System.currentTimeMillis() - startTime) <= time * 1000) {
			return;
		}		
		
		//删除buff
		this.remove();
		
		final float x = target.getX();
		final float z = target.getZ();
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		target.changeAttr(AttrType.hp, 0);
		target.synchChangeAttr();
		
		int deathCount = 0;	//累计被炸死的人数
		//通知死亡
		mapInstance.death(source, target);
		for (AbstractInstance tank : mapInstance.getAllTank()) {
			if (tank.getId() == target.getId()
					|| target.getTeam() == tank.getTeam()
					|| tank.isDeath()) {
				continue;
			}
			// 判断范围
			float px = Math.abs(tank.getX() - x);
			float pz = Math.abs(tank.getZ() - z);

			int range = (int) Math.sqrt(px * px + pz * pz);
			if (radius > range) {
				if (Util.isEmpty(attrMap)) {
					continue;
				}
				for (Entry<AttrType, Float> entry : attrMap.entrySet()) {
					AttrType attrType = entry.getKey();
					Float value = tank.get(attrType);
					if (value == null) {
						continue;
					}
					value += entry.getValue();
					LogCore.runtime.debug("buff影响属性 {} : {}", attrType, value);
					tank.changeAttr(attrType, value);
				}
				// 同步属性变化
				tank.synchChangeAttr();

				if (tank.isDeath()) {
					mapInstance.death(target, tank);
					deathCount += 1;
				}
			}
		}
		
		//统计被炸死的人数
		if(deathCount > 0){
			target.getQuestRecord().setDeadKillMaxCount(deathCount);
		}
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}
