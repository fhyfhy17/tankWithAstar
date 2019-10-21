package com.ourpalm.tank.script.buff;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_DEL_EFFECT_MSG;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 *	时效性buff
 */
public class Buff implements IBuff{

	protected int id;						//实例id
	protected int templateId;				//模版id
	protected AbstractInstance source;		//释放者
	protected AbstractInstance target;		//作用效果的目标
	protected int time; 				//时常(单位:秒)
	protected long startTime = System.currentTimeMillis();	//启动时间
	protected Map<AttrType, Float> attrMap = new HashMap<>(); 	//所影响的属性
	protected int effectId;				//效果id
	
	@Override
	public void startup() {
		this.startTime = System.currentTimeMillis();
		//启动时，添加改变属性
		GameContext.getTankApp().reCalcAttr(target, attrMap);
	}

	@Override
	public void update() {
		final int time = this.time;
		//永久时限
		if(time < 0){
			return ;
		}
		//判断时长
		if((System.currentTimeMillis() - startTime) > time * 1000){
			//销毁逻辑
			this.destroy();
			//删除buff
			this.remove();
			//判断死亡
			this.deathLogic();
		}
		
	}

	//死亡逻辑
	protected void deathLogic(){
		//判断死亡
		if(target.isDeath()){
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
			if(mapInstance == null){
				return ;
			}
			mapInstance.death(source, target);
			//统计被燃烧弹击杀
			source.getQuestRecord().burnFireBulletKillCount(templateId);
		}
	}
	

	//删除buff
	protected void remove(){
		//删除列表
		target.removeBuff(id);
		
		//如果身上还有此相同的效果Buff则不通知客户端去除
		for(IBuff buff : target.getBuffers()){
			if(buff.getEffectId() == effectId){
				return ;
			}
		}
		
		//效果ID为空不通知客户端效果
		if(effectId <= 0){
			return ;
		}
		
		//广播消息
		STC_DEL_EFFECT_MSG msg = STC_DEL_EFFECT_MSG.newBuilder()
				.setEffectId(effectId)
				.setId(target.getId())
				.build();
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
					BATTLE_MSG.CMD_ID.STC_DEL_EFFECT_VALUE, msg.toByteArray());
		}
	}
	

	@Override
	public void clear() {
		this.startTime = 0;
	}
	
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int getTime(){
		return this.time;
	}

	@Override
	public int getEffectId() {
		return effectId;
	}

	private void destroy() {
		if(Util.isEmpty(attrMap)){
			return ;
		}
		Map<AttrType, Float> _attrMap = new HashMap<>(); 
		for(Entry<AttrType, Float> entry : attrMap.entrySet()){
			_attrMap.put(entry.getKey(), entry.getValue() * -1);
		}
		//重算属性
		GameContext.getTankApp().reCalcAttr(target, _attrMap);
	}
	

	@Override
	public void resetTime() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public int getTemplateId() {
		return templateId;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public void setSource(AbstractInstance source) {
		this.source = source;
	}
	public void setTarget(AbstractInstance target) {
		this.target = target;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public void setAttrMap(Map<AttrType, Float> attrMap) {
		this.attrMap = attrMap;
	}
	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}
}
