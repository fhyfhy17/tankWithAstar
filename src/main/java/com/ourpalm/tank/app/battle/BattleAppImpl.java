package com.ourpalm.tank.app.battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.RoleWarInfoDao;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.HIT_INCOME_INFO;
import com.ourpalm.tank.message.BATTLE_MSG.HitType;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_ICON_TYPE;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_TYPE;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HIT_INCOME_ICON_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HIT_INCOME_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HIT_MSG;
import com.ourpalm.tank.message.MATCH_MSG.AiItem;
import com.ourpalm.tank.message.MATCH_MSG.AiType;
import com.ourpalm.tank.message.MATCH_MSG.BuildItem;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.PropItem;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.GoodsShellTemplate;
import com.ourpalm.tank.template.IncomeTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.type.HitPart;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.IncomeInfo;
import com.ourpalm.tank.vo.MapInstance;

public class BattleAppImpl implements BattleApp {

	private RoleWarInfoDao roleWarInfoDao;
	private Map<Integer, RoleWarInfo> roleWarInfoMap = new ConcurrentHashMap<>();

	@Override
	public void login(int roleId, boolean nextDay) {
		RoleWarInfo roleWarInfo = this.roleWarInfoDao.get(roleId);
		roleWarInfoMap.put(roleId, roleWarInfo);

		if (!nextDay) {
			return;
		}
		int freeReviveCount = GameContext.getVipApp().getPrivilegeAliveCount(roleId);
		if (freeReviveCount <= 0) {
			return;
		}
		roleWarInfo.setFreeReviveCount(freeReviveCount);
		this.roleWarInfoDao.save(roleWarInfo);
	}

	@Override
	public void offline(int roleId) {
		roleWarInfoMap.remove(roleId);
	}

	@Override
	public TankItem buildTankItem(AbstractInstance tank, AttrType[] attrs) {

		Location location = Location.newBuilder().setX(tank.getX()).setY(tank.getY()).setZ(tank.getZ()).setDir(tank.getBirthLocation().getDir()).build();
		TankItem.Builder itemBuilder = TankItem.newBuilder();
		itemBuilder.setId(tank.getId());
		itemBuilder.setTeamType(tank.getTeam());
		itemBuilder.setTankId(tank.getTemplateId());
		itemBuilder.setRoleName(tank.getRoleName());
		itemBuilder.setBirthLocation(location);
		itemBuilder.addAllPropes(this.getPropItem(tank, attrs));
		itemBuilder.setMain(tank.getAiMain());
		itemBuilder.setElite(tank.isEliteTank());
		itemBuilder.setRobot(tank.isRobot() ? 1 : 0);
		itemBuilder.setAiItem(AiItem.newBuilder().setAiType(AiType.valueOf(tank.getAiType())).setAiStr(tank.getAiStr()).setAiInt(tank.getAiInt()).build());
		itemBuilder.setAtk(GameContext.getTankApp().calcTankAndPart2Atk(tank));
		itemBuilder.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(tank.getRoleId()));
		itemBuilder.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(tank.getRoleId()));
		return itemBuilder.build();
	}

	@Override
	public BuildItem buildBuildItem(AbstractInstance tank) {
		BuildItem.Builder builder = BuildItem.newBuilder();
		builder.setId(tank.getId());
		builder.setBuildId(tank.getTemplateId());
		builder.setName(tank.getRoleName());
		builder.setBirthLocation(tank.getBirthLocation());
		builder.setTeamType(tank.getTeam());
		builder.addAllPropes(this.getPropItem(tank, BattleApp.otherTankAttr));
		builder.setRobot(tank.isRobot() ? 1 : 0);
		builder.setAiItem(AiItem.newBuilder().setAiType(AiType.valueOf(tank.getAiType())).setAiStr(tank.getAiStr()).setAiInt(tank.getAiInt()).build());
		return builder.build();
	}

	// 封装坦克属性
	private List<PropItem> getPropItem(AbstractInstance tank, AttrType[] attrs) {
		List<PropItem> list = new ArrayList<>();
		Map<AttrType, Float> attrMap = tank.getAttribute();
		for (AttrType attr : attrs) {
			PropItem.Builder builder = PropItem.newBuilder();
			builder.setId(attr.getNumber());
			builder.setValue(attrMap.get(attr));
			// LogCore.runtime.debug("{} : {}", attr, attrMap.get(attr));
			list.add(builder.build());
		}
		return list;
	}

	@Override
	public void hitTank(HitParam param) {
		final AbstractInstance target = param.getTarget();
		final AbstractInstance source = param.getSource();

		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if (mapInstance == null) {
			return;
		}
		// 是否可开火
		if (target.isDeath() || !source.view(target)) {// 是否进入视野
			// || !source.fireRange(target)){ //进入射程
			// || !source.isFireElevation(target)){ //俯仰角判断
			// || source.hadBarrier(target)){ //是否有阻挡
			return;
		}

		// 使用技能逻辑
		this.useSkill(param);

		// 计算金币弹加成
		this.calcGoldShellHit(param);

		// 计算伤害
		this.calcDamage(param);

		// 判断触发受损效果
		this.hitHarmEffect(param);

		// 累计助攻
		target.putKiller(source.getId(), System.currentTimeMillis());

		// 触发被动技能
		Collection<Skill> allSkills = source.getAllSkill();
		if (!Util.isEmpty(allSkills)) {
			for (Skill skill : allSkills) {
				skill.hit(param.getFireType(), target);
			}
		}
	}

	// ====================新伤害公式========================

	public void hitTankNew(HitParam param) {

		final AbstractInstance target = param.getTarget();
		final AbstractInstance source = param.getSource();
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if (mapInstance == null) {
			return;
		}
		// 是否可开火
		if (target.isDeath() || !source.view(target)) {// 是否进入视野
			return;
		}

		// 计算伤害
		float finalHit = 0;
		float minHit = source.get(AttrType.n_minHit);// 最小伤害值
		float maxHit = source.get(AttrType.n_maxHit);// 最大伤害值
		float chuatou = source.get(AttrType.n_stab);// 穿透力

		float armor = 0;// 甲厚度
		AttrType armorAttrType = getJiaHouDuType(param.getHitPart(), source);
		if (armorAttrType == null) {
			LogCore.runtime.error("hitPart错误 未找到 part=[]  ", param.getHitPart());
			return;
		}
		armor = source.get(armorAttrType);
		float critPercent = source.get(AttrType.n_crit);// 暴击率
		float critAdd = 0.8f;// 暴击加成

		float near = source.get(AttrType.n_isNear);// 近程伤害百分比
		float far = source.get(AttrType.n_isFar);// 远程伤害百分比

		float distance = Util.range(source.getX(), source.getZ(), target.getX(), target.getZ()); // 距离
		float dr = target.get(AttrType.n_dr);// 伤害减免
		// 穿甲弹加穿透值
		chuatou = chuatou + getChuanJiaChuanTouAdd(source, chuatou);

		boolean ifStab = ifStab(param, armor, chuatou);
		if (!ifStab) {
			hitDeal(param, 0, HitType.dodge);
			// 收益
			sendHitIcon(source, INCOME_ICON_TYPE.INCOME_ICON_UNSTAB);
			return;
		}

		// 基础伤害
		float baseHit = baseHit(param, minHit, maxHit);
		// 穿甲弹加伤害值
		float hitAdd = baseHit + getChuanJiaAtkAdd(source, baseHit);

		// 距离加成的伤害
		float distanceAdd = distanceCalc(param, distance, near, far, baseHit);
		finalHit = baseHit + distanceAdd + hitAdd;
		// 暴击加成
		boolean ifCrit = crit(param, critPercent);
		if (ifCrit) {
			finalHit = finalHit + finalHit * critAdd;
			// 收益
			sendHitIcon(source, INCOME_ICON_TYPE.INCOME_ICON_CRIT);
		}
		// 伤害减免
		finalHit = finalHit - finalHit * dr;
		// 正式发送伤害值
		boolean ifDie = hitDeal(param, finalHit, HitType.common);
		// 累计助攻
		target.putKiller(source.getId(), System.currentTimeMillis());
		// 收益
		buildIncomeInfo(source, target, finalHit, false, ifStab, ifCrit, ifDie);
	}

	private float getChuanJiaChuanTouAdd(AbstractInstance source, float chantou) {
		int goodsId = source.getBattleGoods();
		if (goodsId <= 0) {
			return 0;
		}
		final GoodsShellTemplate template = GameContext.getGoodsApp().getGoodsShellTemplate(goodsId);
		if (template == null) {
			return 0;
		}
		return template.getChuajia() * chantou;
	}

	private float getChuanJiaAtkAdd(AbstractInstance source, float atk) {
		int goodsId = source.getBattleGoods();
		if (goodsId <= 0) {
			return 0;
		}
		final GoodsShellTemplate template = GameContext.getGoodsApp().getGoodsShellTemplate(goodsId);
		if (template == null) {
			return 0;
		}
		return template.getAtk() * atk;
	}
	
	private void buildHitIncomeInfo(STC_HIT_INCOME_MSG.Builder builder, INCOME_TYPE type, AbstractInstance source, int damage) {

		TankTemplate t = GameContext.getTankApp().getTankTemplate(source.getTemplateId());
		if (t == null) {
			LogCore.runtime.error("构建坦克收益时，找不到坦克templateId  = {}", source.getTemplateId());
			return;
		}
		int tankLevel = t.getLevel_i();

		Map<Integer, IncomeTemplate> tankIncome = GameContext.getMapApp().getIncomeTemplateMap();
		IncomeTemplate incomeTemplate = tankIncome.get(tankLevel);
		if (incomeTemplate == null) {
			LogCore.runtime.error("构建坦克收益时，找不到坦克IncomeTemplate level  = {}", tankLevel);
			return;
		}
		int iron = 0;
		int exp = 0;
		switch (type) {
		case INCOME_TYPE_FIND:
			iron = incomeTemplate.getFindIron();
			exp = incomeTemplate.getFindExp();
			break;
		case INCOME_TYPE_CRIT:
			iron = (int) (damage * 1.0f / incomeTemplate.getCritIron());
			exp = (int) (damage * 1.0f / incomeTemplate.getCritExp());
			break;
		case INCOME_TYPE_HITDIE:
			iron = incomeTemplate.getHitDieIron();
			exp = incomeTemplate.getHitDieExp();
			break;
		case INCOME_TYPE_STAB:
			iron = (int) (damage * 1.0f / incomeTemplate.getStabIron());
			exp = (int) (damage * 1.0f / incomeTemplate.getStabExp());
			break;

		default:
			break;
		}
		// 加入收益
		putIncome(source.getIncomeMap(), type, iron, exp);

		HIT_INCOME_INFO.Builder infoBuilder = HIT_INCOME_INFO.newBuilder();
		infoBuilder.setType(type);
		infoBuilder.setExp(exp);
		infoBuilder.setIron(iron);
		builder.addInfos(infoBuilder);
	}

	private void putIncome(Map<INCOME_TYPE, List<IncomeInfo>> map, INCOME_TYPE type, int iron, int exp) {
		List<IncomeInfo> list = map.get(type);
		if (list == null) {
			list = new ArrayList<IncomeInfo>();
		}
		IncomeInfo in = new IncomeInfo();
		in.setExp(exp);
		in.setIron(iron);
		in.setHonor(0);
		in.setType(type);
		in.setIncomeTime(System.currentTimeMillis());

		list.add(in);
		map.put(type, list);
	}

	public void buildIncomeInfo(AbstractInstance source, AbstractInstance target, float damage, boolean find, boolean stab, boolean crit, boolean hitdie) {
		if (target == null) {
			LogCore.runtime.info("收益里传进来的 target是空  ");
			return;
		}
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(source.getRoleId());
		STC_HIT_INCOME_MSG.Builder builder = STC_HIT_INCOME_MSG.newBuilder();
		builder.setSourceId(source.getId());
		builder.setTargetId(target.getId());
		builder.setDamage((int) damage);
		if (find) {
			// 给被发现者发图标
			sendHitIcon(target, INCOME_ICON_TYPE.INCOME_ICON_BEFOUND);
			int findLimit = GameContext.getMapApp().getExtraIncomeTemplate().getFindLimit();
			List<IncomeInfo> findList = source.getIncomeMap().get(INCOME_TYPE.INCOME_TYPE_FIND);
			if (findList == null) {
				source.getIncomeMap().put(INCOME_TYPE.INCOME_TYPE_FIND, new ArrayList<IncomeInfo>());
				findList = source.getIncomeMap().get(INCOME_TYPE.INCOME_TYPE_FIND);
			}
			if (findList.size() >= findLimit) {
				return;
			}
			buildHitIncomeInfo(builder, INCOME_TYPE.INCOME_TYPE_FIND, source, (int) damage);
			if (connect != null) {
				// 给发现者发收益
				connect.sendMsg(builder.build());
			}
			return;
		}
		if (crit) {
			stab = false;
			buildHitIncomeInfo(builder, INCOME_TYPE.INCOME_TYPE_CRIT, source, (int) damage);
		}
		if (stab) {
			buildHitIncomeInfo(builder, INCOME_TYPE.INCOME_TYPE_STAB, source, (int) damage);
		}
		if (hitdie) {
			buildHitIncomeInfo(builder, INCOME_TYPE.INCOME_TYPE_HITDIE, source, (int) damage);
		}
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	@Override
	public void sendHitIcon(AbstractInstance tank, INCOME_ICON_TYPE type) {
		if (tank.getRoleId() > 0) {
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
			if (connect != null) {
				STC_HIT_INCOME_ICON_MSG.Builder builder = STC_HIT_INCOME_ICON_MSG.newBuilder();
				builder.setType(type);
				connect.sendMsg(builder.build());
			}
		}
	}

	private AttrType getJiaHouDuType(int hitPart, AbstractInstance source) {
		HitPart hPart = HitPart.values()[hitPart];
		switch (hPart) {
		case TurretFront:
			return AttrType.n_ArmorTurretFront;
		case TurretMiddle:
			return AttrType.n_ArmorTurretMiddle;
		case Turretbehind:
			return AttrType.n_ArmorTurretbehind;
		case BodyFront:
			return AttrType.n_ArmorBodyFront;
		case BodyMiddle:
			return AttrType.n_ArmorBodyMiddle;
		case Bodybehind:
			return AttrType.n_ArmorBodybehind;
		default:
			break;
		}
		return null;
	}

	private boolean crit(HitParam param, float crit) {
		if (Math.random() < crit) {
			return true;
		}
		return false;
	}

	private float baseHit(HitParam param, float minHit, float maxHit) {
		float result = minHit + (float) (Math.random() * ((maxHit - minHit) + 1));
		return result;
	}

	private float distanceCalc(HitParam param, float distance, float near, float far, float baseHit) {
		float add = 0;
		if (distance >= 30 && distance <= 100) {
			float a = (1 - (distance - 30) * 1.0f / 70);
			if (a >= 1) {
				a = 1;
			}
			add = a * near * baseHit;
		}
		if (distance >= 150 && distance <= 220) {
			float a = (1 - (distance - 150) * 1.0f / 70);
			if (a >= 1) {
				a = 1;
			}
			add = a * far * baseHit;
		}
		if (add <= 0) {
			return 0;
		}
		return add;
	}

	private void hitMessage(HitParam param, float damage, HitType type) {
		AbstractInstance source = param.getSource();
		AbstractInstance target = param.getTarget();
		if (damage <= 0) {
			// 伤害漂字
			STC_HIT_MSG hitMsg = STC_HIT_MSG.newBuilder().setDamage((int) damage).setSourceId(source.getId()).setTargetId(target.getId()).setType(type).setHitPart(param.getHitPart())
					.setFireType(FireType.immune).setStdItem(param.getItemId()).build();

			// 跟双方发送伤害消息
			source.sendMsg(hitMsg);
			target.sendMsg(hitMsg);
		} else {
			// 伤害漂字
			STC_HIT_MSG hitMsg = STC_HIT_MSG.newBuilder().setDamage((int) damage).setSourceId(source.getId()).setTargetId(target.getId()).setType(type).setHitPart(param.getHitPart())
					.setFireType(FireType.fire).setStdItem(param.getItemId()).build();

			// 跟双方发送伤害消息
			source.sendMsg(hitMsg);
			target.sendMsg(hitMsg);
		}
	}

	private boolean hitDeal(HitParam param, float damage, HitType type) {
		AbstractInstance source = param.getSource();
		AbstractInstance target = param.getTarget();
		hitMessage(param, damage, type);
		// 设置最后受到伤害值，计算协助伤害使用
		target.setLastDamage((int) damage);
		if (damage <= 0) {
			return false;
		}

		int currHp = (int) (target.get(AttrType.hp) - damage);
		if (currHp < 0) {
			currHp = 0;
		}
		target.changeAttr(AttrType.hp, currHp);

		// 统计输出伤害和承受伤害
		source.setHitAllDamage((int) (source.getHitAllDamage() + damage));
		target.setReceiveAllDamage((int) (target.getReceiveAllDamage() + damage));

		// 属性同步
		target.synchChangeAttr();

		// 处理死亡
		if (currHp <= 0) {
			// TODO 收益 sendHitIcon(source, INCOME_ICON_TYPE.INCOME_ICON_HITDIE);
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(source.getMapInstanceId());
			if (mapInstance != null) {
				mapInstance.death(source, target);
				// 统计死于燃烧弹技能
				source.getQuestRecord().burnFireBulletKillCountBySkill(param.getItemId());
			}
			return true;
		}
		return false;
	}

	private boolean ifStab(HitParam param, float jiaHoudu, float chuatou) {
		float chuatouLv = (jiaHoudu - chuatou - 235.17f) / -272.34f;
		if (Math.random() < chuatouLv)
			return true;
		return false;
	}

	// ====================新伤害公式 end========================
	// 使用成员技能
	private void useSkill(HitParam param) {
		if (param.getFireType() != FireType.skill) {
			return;
		}
		final AbstractInstance tank = param.getSource();
		Skill skill = tank.getSkill(param.getItemId());
		if (skill == null) {
			return;
		}
		skill.use(param.getTarget());
	}

	// 计算金币弹附加伤害
	private void calcGoldShellHit(HitParam param) {
		if (param.getFireType() != FireType.goods) {
			return;
		}
		final int goodsId = param.getItemId();
		if (goodsId <= 0) {
			return;
		}
		final AbstractInstance source = param.getSource();
		// 判断数量
		int count = source.getGoods(goodsId);
		if (count <= 0) {
			return;
		}
		final GoodsShellTemplate template = GameContext.getGoodsApp().getGoodsShellTemplate(goodsId);
		if (template == null) {
			return;
		}

		TankTemplate tankTmp = GameContext.getTankApp().getTankTemplate(source.getTemplateId());
		if (tankTmp == null) {
			return;
		}
		GoodsBaseTemplate goodsTmp = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		// 判断炮弹是否为该坦克所使用的类型
		if (goodsTmp.getType_i() != tankTmp.getShellType_i()) {
			return;
		}

		param.setChuanjiaAddRat(template.getChuajia());
		param.setAtkAddRat(template.getAtk());
	}

	// 计算伤害
	private void calcDamage(HitParam param) {
		// 是否跳弹
		if (param.isHadDodge()) {
			this.calcDodgeDamage(param);
			return;
		}
		// 是否穿甲
		if (this.calcChuanjia(param)) {
			return;
		}
		// 普通实弹伤害
		this.calcCommon(param);
	}

	/** 触发被动技能是否免疫部件受损 */
	private boolean passiveSkillImmunePartHitEffect(AbstractInstance target) {
		boolean hadImmune = false;
		for (Skill skill : target.getAllSkill()) {
			if (skill.hadImmuneHitEffect()) {
				hadImmune = true;
			}
		}
		return hadImmune;
	}

	// 触发受损
	private void hitHarmEffect(HitParam param) {
		final AbstractInstance target = param.getTarget();
		final AbstractInstance source = param.getSource();

		// 触发被动技能
		if (passiveSkillImmunePartHitEffect(target)) {
			return;
		}

		final int hitPart = param.getHitPart();
		TankTemplate template = GameContext.getTankApp().getTankTemplate(target.getTemplateId());
		if (template == null) {
			return;
		}
		int buffId = template.getHitPartBuff(target.randomPeshe(hitPart));
		if (buffId <= 0) {
			return;
		}
		// 向目标添加受损buff
		GameContext.getBuffApp().putBuff(source, target, buffId);
	}

	// 计算跳弹伤害
	private boolean calcDodgeDamage(HitParam param) {
		// 跳弹伤害 = 实弹伤害 * 跳弹伤害倍率
		int damage = (int) (this.ammoDamage(param) * param.getSource().get(AttrType.mul_dodge));

		// logger.info("{} --> {} 跳弹伤害:{}", source.getId(), target.getId(),
		// damage);

		// 跳弹伤害
		this.attact(param, HitType.dodge, damage);

		return true;
	}

	// 计算穿甲伤害
	private boolean calcChuanjia(HitParam param) {
		final AbstractInstance source = param.getSource();
		final AbstractInstance target = param.getTarget();
		final int hitPart = param.getHitPart();

		// 计算穿甲值 = 穿甲值 * (1 - 浮动范围 + 2 * random() * 浮动范围)
		final float chuanjia = source.get(AttrType.chuanjia) * (1 + param.getChuanjiaAddRat()); // 金币弹加成
		final float chuanjia_rat = source.get(AttrType.chuanjia_rat); // 穿甲浮动范围
		int chuanjiaValue = (int) (chuanjia * (1 - chuanjia_rat + 2 * Math.random() * chuanjia_rat));
		float defValue = this.getDefValue(target, hitPart);
		if (chuanjiaValue < defValue) {
			return false;
		}
		// 穿甲伤害 = 实弹伤害 * 穿甲暴击倍率
		int damage = (int) (this.ammoDamage(param) * source.get(AttrType.mul_chuanjia));

		// logger.info("{} --> {} 穿甲伤害:{}", source.getId(), target.getId(),
		// damage);

		// 设置穿甲，被动技能使用
		param.setHadChuanjia(true);

		// 穿甲
		this.attact(param, HitType.chuajia, damage);

		return true;
	}

	// 计算普通实弹伤害
	private void calcCommon(HitParam param) {
		float damage = this.ammoDamage(param);

		// logger.info("{} --> {} 实弹伤害:{}", source.getId(), target.getId(),
		// damage);

		this.attact(param, HitType.common, damage);
	}

	// 实弹伤害值
	private float ammoDamage(HitParam param) {
		// 攻击 * (1 + 0.1 * random * (1 - Math.pow(1 - (Math.pow(Math.random(),
		// 2)), 0.5))) * 伤害减免 * 位置伤害系数
		final AbstractInstance source = param.getSource();
		final AbstractInstance target = param.getTarget();
		final int hitPart = param.getHitPart();
		final float atk = source.get(AttrType.atk) * (1 + param.getAtkAddRat()); // 金币弹的伤害加成
		float random = Math.random() > 0.5 ? 1 : -1;
		return (float) (atk * (1 + 0.1 * random * (1 - Math.pow(1 - (Math.pow(Math.random(), 2)), 0.5))) * target.get(AttrType.derate) * this.getHitPartRatio(hitPart));
	}

	// public static void main(String[] args){
	// for(int i = 0; i < 100; i++){
	// float random = Math.random() > 0.5 ? 1 : -1;
	// System.out.println((1 + 0.25 * random * (1 - Math.pow(1 -
	// (Math.pow(Math.random(), 2)), 0.5))));
	// }
	// }

	private void attact(HitParam param, HitType type, float damage) {
		final AbstractInstance target = param.getTarget();
		final AbstractInstance source = param.getSource();
		// 无伤害不触发任何效果
		if (damage <= 0) {
			// 伤害漂字
			STC_HIT_MSG hitMsg = STC_HIT_MSG.newBuilder().setDamage((int) damage).setSourceId(source.getId()).setTargetId(target.getId()).setType(type).setHitPart(param.getHitPart())
					.setFireType(FireType.immune).setStdItem(param.getItemId()).build();

			// 跟双方发送伤害消息
			source.sendMsg(hitMsg);
			target.sendMsg(hitMsg);
			return;
		}

		// 被动技能影响伤害
		damage = this.calcPassiveSkillHit(param, damage);

		// 设置最好受到伤害值，计算协助伤害使用
		target.setLastDamage((int) damage);

		if (damage <= 0) {
			return;
		}
		int currHp = (int) (target.get(AttrType.hp) - damage);
		if (currHp < 0) {
			currHp = 0;
		}
		target.changeAttr(AttrType.hp, currHp);

		// 触发目标的被动技能
		for (Skill skill : target.getAllSkill()) {
			skill.currHpChange();
		}

		// 统计输出伤害和承受伤害
		source.setHitAllDamage((int) (source.getHitAllDamage() + damage));
		target.setReceiveAllDamage((int) (target.getReceiveAllDamage() + damage));

		// 伤害漂字
		STC_HIT_MSG hitMsg = STC_HIT_MSG.newBuilder().setDamage((int) damage).setSourceId(source.getId()).setTargetId(target.getId()).setType(type).setHitPart(param.getHitPart())
				.setFireType(param.getFireType()).setStdItem(param.getItemId()).build();

		// 跟双方发送伤害消息
		source.sendMsg(hitMsg);
		target.sendMsg(hitMsg);

		// 属性同步
		target.synchChangeAttr();

		// 处理死亡
		if (currHp <= 0) {
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(source.getMapInstanceId());
			if (mapInstance != null) {
				mapInstance.death(source, target);
				// 统计死于燃烧弹技能
				source.getQuestRecord().burnFireBulletKillCountBySkill(param.getItemId());
				return;
			}
		}
	}

	// 被动技能影响伤害值
	private float calcPassiveSkillHit(HitParam param, float damage) {
		final AbstractInstance target = param.getTarget();
		final AbstractInstance source = param.getSource();
		Collection<Skill> sourceAllSkills = source.getAllSkill();
		Collection<Skill> targetAllSkills = target.getAllSkill();

		if (!Util.isEmpty(sourceAllSkills)) {
			// 被动技能伤害加成
			for (Skill skill : sourceAllSkills) {
				damage += skill.attackDamagePlusValue(param, damage);
			}
			// 被动技能伤害翻倍
			for (Skill skill : sourceAllSkills) {
				damage *= skill.attackDamagePlusRat(param);
			}
		}

		if (!Util.isEmpty(targetAllSkills)) {
			// 防御方的伤害减免
			for (Skill skill : targetAllSkills) {
				damage += skill.defenseDamageValue(source, damage);
			}
		}
		return damage;
	}

	// 命中部位 0前,1侧,2后
	// 获取护甲值
	private float getDefValue(AbstractInstance target, int hitPart) {
		HitPart part = HitPart.values()[hitPart];
		switch (part) {
		case TurretFront:
			return target.get(AttrType.fdef);
		case TurretMiddle:
			return target.get(AttrType.idef);
		case Turretbehind:
			return target.get(AttrType.bdef);
		default:
			break;
		}
		return 0f;
	}

	private float getHitPartRatio(int hitPart) {
		HitPart part = HitPart.values()[hitPart];
		switch (part) {
		case TurretFront:
			return 1f;
		case TurretMiddle:
			return 1f;
		case Turretbehind:
			return 1f;
		case BodyFront:
			return 1f;
		case BodyMiddle:
			return 1f;
		case Bodybehind:
			return 1f;
		default:
			break;
		}
		return 0f;
	}

	public void setRoleWarInfoDao(RoleWarInfoDao roleWarInfoDao) {
		this.roleWarInfoDao = roleWarInfoDao;
	}

	@Override
	public RoleWarInfo getRoleWarInfo(int roleId) {
		RoleWarInfo roleWarInfo = this.roleWarInfoMap.get(roleId);
		if (roleWarInfo == null) {
			return this.roleWarInfoDao.get(roleId);
		}
		return roleWarInfo;
	}

	@Override
	public void saveRoleWarInfo(RoleWarInfo roleWarInfo) {
		this.roleWarInfoDao.save(roleWarInfo);
	}
}
