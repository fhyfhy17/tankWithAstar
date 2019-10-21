package com.ourpalm.tank.script.skill;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_SKILL_EFFECT_MSG;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public abstract class Skill {
	private final static int active = 1;
	private final static int ALL_RAT = 10000;
	protected final static Logger logger = LogCore.runtime;
	protected int rat;					//触发几率
	protected int coolTime;				//冷却时间(单位：秒)
	protected AbstractInstance selfTank; 	//技能拥有者
	protected int skillId;
	protected int type; 				//1:主动  2:被动
	
	private long lastUseTime ;		//上次使用技能时间
	private boolean hadCoolTime; 		//是否已经冷却,主动技能使用
	
	public Skill(AbstractInstance selfTank, SkillTemplate template){
		this.rat = (int)(template.getRat() * ALL_RAT);
		this.coolTime = template.getCoolTime();
		this.selfTank = selfTank;
		this.skillId = template.getId();
		this.type = template.getType();
	}
	
	public int getId(){
		return this.skillId;
	}
	
	public boolean isActive(){
		return type == Skill.active;
	}
	
	
	/** 同步技能效果 */
	protected void notifyEffectMsg(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(selfTank.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.brodcastMsg(
					BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
					BATTLE_MSG.CMD_ID.STC_USE_SKILL_EFFECT_VALUE, 
					STC_USE_SKILL_EFFECT_MSG.newBuilder()
					.setId(selfTank.getId())
					.setSkillId(getId())
					.build().toByteArray());
		}
	}
	
	/** 
	 * 判断冷却时间(用于Action提前判断技能是否冷却,需要给用户提示) 
	 * 被动技能触发应使用该方法
	 * */
	public boolean finishCoolTime(){
		if((System.currentTimeMillis() - lastUseTime) > this.coolTime * 1000){
			hadCoolTime = true;
			this.lastUseTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	
	/** 重置冷却时间(用于主动技能脚本验证使用) */
	protected boolean hadCoolTime(){
		if(this.hadCoolTime){
			this.hadCoolTime = false;
			return true;
		}
		return false;
	}
	
	
	/** 被动技能是否触发 */
	protected boolean passiveTrigger(){
		//先判断触发几率
		//后判断技能冷却
		return this.triggerRat() && this.finishCoolTime();
	}
	
	
	/** 判断触发几率 */
	public boolean triggerRat(){
		return RandomUtil.randomInt(ALL_RAT) < rat;
	}
	
	
	/** 抵挡伤害值 */
	public float defenseDamageValue(AbstractInstance attck, float damage){
		return 0;
	}
	
	
	/** 攻击伤害加成值 */
	public float attackDamagePlusValue(HitParam hirParam, float damage){
		return 0;
	}
	
	
	/** 攻击伤害加成倍率 */
	public float attackDamagePlusRat(HitParam hirParam){
		return 1;
	}
	
	
	
	/** 使用技能 */
	public void use(AbstractInstance target){}

	
	
	/** 血量改变时 */
	public void currHpChange(){}
	
	
	/** 死亡时触发效果 返回true外围应不发送死亡通知 */
	public boolean death(){
		return false;
	}
	
	/** 攻击时 */
	public void fire(){}
	
	/** 命中时 */
	public void hit(FireType fireType, AbstractInstance target){}
	
	/** 免疫buff */
	public boolean hadImmuneBuff(int buffId){
		return false;
	}
	
	/** 免疫伤害效果(履带断裂、油箱起火、发动机损坏、弹药架损坏、炮塔受损) */
	public boolean hadImmuneHitEffect(){
		return false;
	}

	/** 不记录击杀数 */
	public boolean hadIngoreKillCountWhenDeath() {
		return false;
	}
}
