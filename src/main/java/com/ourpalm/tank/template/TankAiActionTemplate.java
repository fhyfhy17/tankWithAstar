package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.peshe.AIFirePeshe;

public class TankAiActionTemplate {
	
	private int id;				//军衔ID
	private int fireRat;		//普通开火概率
	private int goldFireRat;	//金币弹概率
	private int skillFireRat;	//使用技能概率
	private int goodsRat;		//负面效果BUFF时使用道具概率
	private int pathId;			//出身复活行进路径
	private int	group;			//随机携带道具组
	
	private List<AIFirePeshe> fireList = new ArrayList<>();
	
	public void init(){
		this.buildFire(FireType.fire, fireRat);
		this.buildFire(FireType.goods, goldFireRat);
		this.buildFire(FireType.skill, skillFireRat);
	}
	
	private void buildFire(FireType fireType, int rate){
		AIFirePeshe peshe = new AIFirePeshe();
		peshe.setFireType(fireType);
		peshe.setGon(rate);
		fireList.add(peshe);
	}
	
	/** 随机AI开火类型 */
	public FireType randomFire(){
		AIFirePeshe random = RandomUtil.getPeshe(fireList);
		if(random == null){
			return FireType.fire;
		}
		return random.getFireType();
	}
	
	
	/** 是否使用道具清除BUFF */
	public boolean isUseGoods(){
		int random = RandomUtil.randomInt(100);
		return random < goodsRat;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFireRat() {
		return fireRat;
	}
	public void setFireRat(int fireRat) {
		this.fireRat = fireRat;
	}
	public int getGoldFireRat() {
		return goldFireRat;
	}
	public void setGoldFireRat(int goldFireRat) {
		this.goldFireRat = goldFireRat;
	}
	public int getSkillFireRat() {
		return skillFireRat;
	}
	public void setSkillFireRat(int skillFireRat) {
		this.skillFireRat = skillFireRat;
	}
	public int getGoodsRat() {
		return goodsRat;
	}
	public void setGoodsRat(int goodsRat) {
		this.goodsRat = goodsRat;
	}
	public int getPathId() {
		return pathId;
	}
	public void setPathId(int pathId) {
		this.pathId = pathId;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
}
