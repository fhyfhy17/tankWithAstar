package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_FIRE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.TankPreseasonDeal;
import com.ourpalm.tank.vo.behaviortree.DataContext;

public class FireAction extends MapNode {

	public static final String WAIT_TIME_COUNT = "FireAction.waitTimeCount";
	public static final String FIRE_LOOP       = "FireAction.fireLoop";
	public static final String FIRE_MODE       = "FireAction.fireMode";

	public FireAction(MapInstance map) {
		super(map);
	}

	@Override
	public void init(AbstractInstance tank) {
		DataContext dc = tank.getBahaviorDataContext();
		dc.putBean(FIRE_LOOP, new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME));
		dc.putInt(WAIT_TIME_COUNT, 0);
		dc.putBoolean(FIRE_MODE, false);
	}

	@Override
	public void reset(AbstractInstance tank) {
		DataContext dc = tank.getBahaviorDataContext();
		dc.remove(FIRE_LOOP);
	}

	@Override
	public boolean eval(AbstractInstance tank) {

		LoopCount loopCount = tank.getBahaviorDataContext().getBean(FIRE_LOOP, LoopCount.class);
		if (tank.isDeath() || (loopCount != null && !loopCount.isReachCycle())) {
			return true;
		}

		AbstractInstance target = tank.getFireTarget();
		if (target == null) {
			return true;
		}
		
		// 设置攻击者
		target.setAttacker(tank);

		// 使用战场道具
		useGoods(tank, target);

		// 判断是否命中
		if (hadHitTank(tank, target)) {
			LogCore.runtime.info("{} fire", tank.getRoleName());
			// 随机开火类型
			FireType fireType = tank.getAiAction().randomFire();
			switch (fireType) {
			case fire:
				this.fire(tank, target);
				return true;
			case goods:
				this.useGoldShell(tank, target);
				return true;
			case skill:
				this.useSkill(tank, target);
				return true;
			default:
				break;
			}
			return true;
		}

		// 空放一炮
		fireEmpty(tank, target);

		// 同步AI位置，不能停
		tank.syncCoordinate(target, tank.getX(), tank.getY(), tank.getZ());
		LogCore.runtime.info("{} fire", tank.getRoleName());
		return true;
	}

	/** 是否命中目标 */
	private boolean hadHitTank(AbstractInstance tank, AbstractInstance target) {
		float range = Util.range(tank.getX(), tank.getZ(), target.getX(), target.getZ());
		if (range <= 0) {
			range = 0.1f;
		}
		double hitRat = Math.atan2(0.1, 5) / Math.atan2(tank.get(AttrType.focal_radil_static), 5) * 60 / range;

		return Math.random() <= hitRat;
	}

	// 普通开火
	private void fire(AbstractInstance tank, AbstractInstance target) {
		this.brodcastFireMsg(tank, target, FireType.fire, 0, 0, 0);

		HitParam param = new HitParam();
		param.setSource(tank);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));

		TankPreseasonDeal.INSTANCE.preseasonCalc(param, tank, target);
		map.putHitQueue(param);
	}

	// 使用技能
	private void useSkill(AbstractInstance tank, AbstractInstance target) {
		HitParam param = new HitParam();
		param.setSource(tank);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));

		// 判断是否可使用技能
		boolean hadUseKill = false;
		for (Skill skill : tank.getAllSkill()) {
			if (skill.isActive()) {
				if (skill.finishCoolTime()) {
					param.setFireType(FireType.skill);
					param.setItemId(skill.getId());
					hadUseKill = true;
				}
				break;
			}
		}
		// 没触发使用技能，普通开炮
		if (!hadUseKill) {
			this.fire(tank, target);
			return;
		}

		// 使用技能广播
		this.brodcastFireMsg(tank, target, FireType.skill, param.getItemId(), 0, 0);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, tank, target);
		// 计算伤害
		map.putHitQueue(param);
	}

	// 使用金币弹
	private void useGoldShell(AbstractInstance tank, AbstractInstance target) {
		HitParam param = new HitParam();
		param.setSource(tank);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));

		TankTemplate template = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		if (template == null) {
			return;
		}
		int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(template.getShellType_i()));
		int count = tank.getGoods(goodsId);
		if (count > 0) {
			// 道具不足，普通开火
			this.fire(tank, target);
			return;
		}
		tank.putGoods(goodsId, count - 1);
		param.setFireType(FireType.goods);
		param.setItemId(goodsId);

		// 广播使用金币弹
		this.brodcastFireMsg(tank, target, FireType.goods, goodsId, 0, 0);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, tank, target);
		// 计算伤害
		map.putHitQueue(param);
	}

	// 瞎放一炮
	private void fireEmpty(AbstractInstance tank, AbstractInstance target) {
		int[] deviations = { -1, 1 };
		float offsetAngle = RandomUtil.randomInt(10);
		int index1 = RandomUtil.randomInt(deviations.length);
		int index2 = RandomUtil.randomInt(deviations.length);
		float offsetDirX = deviations[index1] * offsetAngle / 80;
		float offsetDirY = deviations[index2] * offsetAngle / 90;
		this.brodcastFireMsg(tank, target, FireType.fire, 0, offsetDirX, offsetDirY);
	}

	// 广播开火消息
	private void brodcastFireMsg(AbstractInstance tank, AbstractInstance target, FireType fireType, int stdItem,
			float skewX, float skewY) {
		tank.setLastFireTime(System.currentTimeMillis());
		STC_AI_FIRE_MSG fireMsg = STC_AI_FIRE_MSG.newBuilder().setSourceId(tank.getId()).setTargetId(target.getId())
				.setFireType(fireType).setHadDodge(false).setStdItem(stdItem).setSkewX(skewX).setSkewY(skewY).build();
		map.brodcastMsg(fireMsg);
		if (target.getId() == 0 && fireType == FireType.fire) {
			tank.setMissCount(tank.getMissCount() + 1);
		}
	}

	private void useGoods(AbstractInstance tank, AbstractInstance target) {
		try {
			int rat = tank.getAiAction().getGoodsRat();
			if (RandomUtil.randomInt(100) > rat || tank.getBuffers().size() <= 0) {
				return;
			}
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
			int count = tank.getGoods(goodsId);
			if (count <= 0) {
				return;
			}
			tank.putGoods(goodsId, count - 1);

			GoodsWarTemplate template = GameContext.getGoodsApp().getGoodsWarTemplate(goodsId);
			// 广播使用效果
			STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder().setId(tank.getId())
					.setGoodsId(goodsId).build();
			map.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE,
					efffectMsg.toByteArray());
			// 删除buff
			for (String strId : template.getDelBuff().split(Cat.comma)) {
				if (Util.isEmpty(strId)) {
					continue;
				}
				int delBuffId = Integer.parseInt(strId);
				GameContext.getBuffApp().remove(tank, delBuffId);
			}
			// 添加buff
			GameContext.getBuffApp().putBuff(tank, tank, template.getAddBuff());
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}
}
