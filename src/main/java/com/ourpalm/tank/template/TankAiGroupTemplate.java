package com.ourpalm.tank.template;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.vo.AbstractInstance;

public class TankAiGroupTemplate {

	private int group;
	private int shellCount;
	private int goodsCount;
	private int activeSkill;
	private int passiveSkill1;
	private int passiveSkill2;
	
	
	public void init(){
		//验证技能
		this.checkSkillId(this.activeSkill);
		this.checkSkillId(this.passiveSkill1);
		this.checkSkillId(this.passiveSkill2);
	}
	
	private void checkSkillId(int skillId){
		if(skillId <= 0){
			return ;
		}
		SkillTemplate template = GameContext.getSkillApp().getSkillTemplate(skillId);
		if(template == null){
			LogCore.startup.error("坦克AI配置表，group = {}  skillId = {}该技能不存在", this.group, skillId);
		}
	}
	
	
	/** 初始化技能 */
	public void initSkill(AbstractInstance tank){
		this.initSkill(tank, activeSkill);
		this.initSkill(tank, passiveSkill1);
		this.initSkill(tank, passiveSkill2);
	}
	
	
	/** 初始化道具 */
	public void initGoods(AbstractInstance tank){
		int tankId = tank.getTemplateId();
		if(this.shellCount > 0){
			TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
			if(template != null){
				int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(template.getShellType_i()));
				tank.putGoods(goodsId, this.shellCount);
			}
		}
		//战场道具
		if(this.goodsCount > 0){
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
			tank.putGoods(goodsId, goodsCount);
		}
	}
	
	
	private void initSkill(AbstractInstance tank, int skillId){
		if(skillId <= 0){
			return ;
		}
		Skill skill = GameContext.getSkillApp().createSkill(tank, skillId);
		if(skill == null){
			return ;
		}
		tank.putSkill(skill);
	}
	
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getShellCount() {
		return shellCount;
	}
	public void setShellCount(int shellCount) {
		this.shellCount = shellCount;
	}
	public int getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}
	public int getActiveSkill() {
		return activeSkill;
	}
	public void setActiveSkill(int activeSkill) {
		this.activeSkill = activeSkill;
	}
	public int getPassiveSkill1() {
		return passiveSkill1;
	}
	public void setPassiveSkill1(int passiveSkill1) {
		this.passiveSkill1 = passiveSkill1;
	}
	public int getPassiveSkill2() {
		return passiveSkill2;
	}
	public void setPassiveSkill2(int passiveSkill2) {
		this.passiveSkill2 = passiveSkill2;
	}
}
