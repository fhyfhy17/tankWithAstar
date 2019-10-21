package com.ourpalm.tank.script.buff;

import java.util.Map.Entry;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.vo.MapInstance;

/**
 * 持续间隔执行效果
 * 
 * @author wangkun
 *
 */
public class Debuff extends Buff{

	private int intervalTime;		//间隔时间(单位:秒)
	private LoopCount loop ;
	

	@Override
	public void startup() {
		startTime = System.currentTimeMillis();
		loop = new LoopCount(intervalTime * 1000, 1000);
	}

	@Override
	public void update() {
		//判断时长
		if((System.currentTimeMillis() - startTime) > this.time * 1000){
			//删除buff
			this.remove();
			//死亡处理
			this.deathLogic();
			return ;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if(mapInstance == null){
			return ;
		}
		//轮询执行
		if(loop.isReachCycle()){
			if(Util.isEmpty(attrMap) || target.isDeath()){
				return ;
			}
			for(Entry<AttrType, Float> entry : attrMap.entrySet()){
				AttrType attrType = entry.getKey();
				Float value = target.get(attrType);
				if(value == null){
					continue;
				}
				value += entry.getValue();
				LogCore.runtime.debug("buff影响属性 {} : {}", attrType, value);
				target.changeAttr(attrType, value);
			}
			//大于最大血量处理
			if(target.get(AttrType.hp) > target.get(AttrType.n_hpMax)){
				target.changeAttr(AttrType.hp, target.get(AttrType.n_hpMax));
			}
			//同步属性变化
			target.synchChangeAttr();
			
			//死亡处理
			this.deathLogic();
		}
	}
}
