package com.ourpalm.tank.app.buff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HIT_EFFECT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_MINE_LOCATION_MSG;
import com.ourpalm.tank.script.buff.Buff;
import com.ourpalm.tank.script.buff.Debuff;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.script.buff.MapBuff;
import com.ourpalm.tank.script.buff.TimeBombBuff;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.BuffTemplate;
import com.ourpalm.tank.type.BuffRepeat;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class BuffAppImpl implements BuffApp{
	private final static Logger logger = LogCore.runtime; 
	private Map<Integer, BuffTemplate> buffMap = new HashMap<>();

	@Override
	public void start() {
		try{
			String sourceFile = XlsSheetType.buffTemplate.getXlsFileName();
			String sheetName = XlsSheetType.buffTemplate.getSheetName();
			List<BuffTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BuffTemplate.class);
			for(BuffTemplate template : list){
				buffMap.put(template.getId(), template);
			}
		}catch(Exception e){
			LogCore.startup.error("", e);
		}
	}
	
	@Override
	public BuffTemplate getBuffTemplate(int buffId){
		return buffMap.get(buffId);
	}
	
	
	//判断被动技能是否免疫该buff
	private boolean hadImmune(AbstractInstance target, int buffId){
		boolean hadImmune = false;
		for(Skill skill : target.getAllSkill()){
			if(skill.hadImmuneBuff(buffId)){
				hadImmune = true;
			}
		}
		return hadImmune;
	}
	
	

	@Override
	public void putBuff(AbstractInstance source, AbstractInstance target, int buffId) {
		BuffTemplate template = this.buffMap.get(buffId);
		if(template == null){
			logger.warn("buff={} 不存在", buffId);
			return ;
		}
		
		//被动技能是否免疫
		if(hadImmune(target, buffId)){
			return ;
		}
		
		//判断是否可叠加
		if(BuffRepeat.no_repeat.getType() == template.getRepeat()){
			IBuff buff = this.findSingleBuff(target, buffId);
			if(buff != null){
//				buff.resetTime();
				return ;
			}
		}
		
		switch(template.getType()){
			case 1 :  this.createDebuff(source, target, template); break;
			case 2 :  this.createBuff(source, target, template); break;
			case 3 :  this.createMapBuff(source, target, template); break;
			case 4 :  this.createTimeBombBuff(source, target, template); break; 
		}
	}
	
	
	@Override
	public void remove(AbstractInstance target, int buffId){
		for(IBuff buff : this.findBuff(target, buffId)){
			buff.clear();
		}
	}
	

	//寻找单个buff
	private IBuff findSingleBuff(AbstractInstance target, int buffId){
		for(IBuff buff : target.getBuffers()){
			if(buff.getTemplateId() == buffId){
				return buff;
			}
		}
		return null;
	}
	
	
	private List<IBuff> findBuff(AbstractInstance target, int buffId){
		List<IBuff> buffes = new ArrayList<>();
		for(IBuff buff : target.getBuffers()){
			if(buff.getTemplateId() == buffId){
				buffes.add(buff);
			}
		}
		return buffes;
	}
	
	
	
	//广播添加buff
	private void brodcastAddBuffMsg(AbstractInstance target, IBuff buff){
		//判断对方身上是否有相同效果buff
//		boolean isBrodcast = true;
//		for(IBuff ibf : target.getBuffers()){
//			if(ibf.getEffectId() == buff.getEffectId()){
//				isBrodcast = false;
//				break;
//			}
//		}
		//添加启动buff
		target.putBuff(buff);
		buff.startup();
		
		//判断是否广播
//		if(!isBrodcast){
//			return ;
//		}
		//BUFF效果为0，不发送效果给客户端
		if(buff.getEffectId() <= 0){
			return ;
		}
		
		//广播效果
		STC_HIT_EFFECT_MSG msg = STC_HIT_EFFECT_MSG.newBuilder()
				.setEffectId(buff.getEffectId())
				.setId(target.getId())
				.setTime(buff.getTime())
				.build();
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
					BATTLE_MSG.CMD_ID.STC_HIT_EFFECT_VALUE, msg.toByteArray());
		}
	}

	
	//创建debuff逻辑
	private void createDebuff(AbstractInstance source, AbstractInstance target, BuffTemplate template){
		if(target.isDeath()){
			return ;
		}
		Debuff buff = new Debuff();
		buff.setId(idFactory.incrementAndGet());
		buff.setAttrMap(template.getAttr(target));
		buff.setEffectId(template.getEffectId());
		buff.setSource(source);
		buff.setTarget(target);
		buff.setTemplateId(template.getId());
		buff.setTime(template.getTime());
		
		this.brodcastAddBuffMsg(target, buff);
	}
	
	
	//创建Buff逻辑
	private void createBuff(AbstractInstance source, AbstractInstance target, BuffTemplate template){
		if(target.isDeath()){
			return ;
		}
		Buff buff = new Buff();
		buff.setAttrMap(template.getAttr(target));
		buff.setEffectId(template.getEffectId());
		buff.setId(idFactory.incrementAndGet());
		buff.setSource(source);
		buff.setTarget(target);
		buff.setTemplateId(template.getId());
		buff.setTime(template.getTime());
		
		this.brodcastAddBuffMsg(target, buff);
	}
	
	
	private void createMapBuff(AbstractInstance source, AbstractInstance target, BuffTemplate template){
		if(source.isDeath()){
			return ;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(source.getMapInstanceId());
		if(mapInstance == null){
			return ;
		}
		
		MapBuff buff = new MapBuff();
		buff.setAttrMap(template.getAttr(source));
		buff.setEffectId(template.getEffectId());
		buff.setId(idFactory.incrementAndGet());
		buff.setMapInstance(mapInstance);
		buff.setRadius(template.getRadius());
		buff.setSource(source);
		buff.setTarget(target);
		buff.setTemplateId(template.getId());
		buff.setTime(template.getTime());
		
		mapInstance.putBuff(buff);
		buff.startup();
		
		//广播消息
		STC_MINE_LOCATION_MSG msg = STC_MINE_LOCATION_MSG.newBuilder()
				.setId(buff.getId())
				.setTeam(source.getTeam().getNumber())
				.setGoodsId(template.getEffectId())
				.setX(source.getX())
				.setY(source.getY())
				.setZ(source.getZ())
				.build();
		mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_MINE_LOCATION_VALUE, msg.toByteArray());
	}
	
	
	private void createTimeBombBuff(AbstractInstance source, AbstractInstance target, BuffTemplate template){
		TimeBombBuff buff = new TimeBombBuff();
		buff.setAttrMap(template.getAttr(target));
		buff.setEffectId(template.getEffectId());
		buff.setId(idFactory.incrementAndGet());
		buff.setRadius(template.getRadius());
		buff.setSource(source);
		buff.setTarget(target);
		buff.setTemplateId(template.getId());
		buff.setTime(template.getTime());
		
		this.brodcastAddBuffMsg(target, buff);
	}
	
	
	@Override
	public void stop() {
	}
}
