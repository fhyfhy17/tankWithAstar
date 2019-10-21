package com.ourpalm.tank.app.map.ai;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_FIRE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.TankPreseasonDeal;

/**
 * 停止并搜寻敌人开火AI
 * 
 * @author fhy
 *
 */
public class StayAndFireAi extends Ai {

	public StayAndFireAi(AbstractInstance self) {
		super(self);
		this.loopCount = new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME);
	}

	@Override
	public void reset(AbstractInstance target) {
		super.reset(target);
	}

	@Override
	public void update() {
		// 死亡状态，将等待复活
		if (self.isDeath()) {
			return;
		}

		if (loopCount.isReachCycle()) {
			// 如果有目标，且能开炮就不再寻找其它目标，如果不行就寻找目标
			if (!isFire(target)) {
				this.searchEnemy();
			}
			// 移动炮管
			this.moveGun();

			// 开始填弹
			this.redloadShell();

			this.fireLogic();

			// 同步AI位置，不能停
			STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG.newBuilder().setId(self.getId()).setPosition(Coordinate3D.newBuilder().setPx(self.getX()).setPy(self.getY()).setPz(self.getZ()))
					.setTarget(target != null ? target.getId() : 0).build();
			mapInstance.brodcastMsg(moveMsg);
		}
	}

	@Override
	public AiType getType() {
		return AiType.stayAndFire;
	}

	private void fireLogic() {
		// 自己死亡、目标死亡或炮弹不可达
		// 切换到移动状态
		if (target == null) {
			return;
		}
		if (!isFire(target)) {
			return;
		}

		// 判断开火冷却
		if (!self.hadAndSetFireCoolTime()) {
			return;
		}

		// 使用战场道具
		this.useGoods();

		// 判断是否命中
		if (hadHitTank()) {
			// 随机开火类型
			FireType fireType = self.getAiAction().randomFire();
			switch (fireType) {
			case fire:
				this.fire();
				return;
			case goods:
				this.useGoldShell();
				return;
			case skill:
				this.useSkill();
				return;
			default:
				break;
			}
			return;
		}

		// 空放一炮
		this.fireEmpty();
	}

	/** 是否命中目标 */
	private boolean hadHitTank() {
		float range = Util.range(self.getX(), self.getZ(), target.getX(), target.getZ());
		if (range <= 0) {
			range = 0.1f;
		}
		double hitRat = Math.atan2(0.1, 5) / Math.atan2(self.get(AttrType.focal_radil_static), 5) * 60 / range;

		return Math.random() <= hitRat;
	}

	// 普通开火
	private void fire() {
		this.brodcastFireMsg(FireType.fire, 0, 0, 0);

		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		mapInstance.putHitQueue(param);
	}

	// 使用技能
	private void useSkill() {
		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));

		// 判断是否可使用技能
		boolean hadUseKill = false;
		for (Skill skill : self.getAllSkill()) {
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
			this.fire();
			return;
		}

		// 使用技能广播
		this.brodcastFireMsg(FireType.skill, param.getItemId(), 0, 0);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		// 计算伤害
		mapInstance.putHitQueue(param);
	}

	// 使用金币弹
	private void useGoldShell() {
		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));

		TankTemplate template = GameContext.getTankApp().getTankTemplate(self.getTemplateId());
		if (template == null) {
			return;
		}
		int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(template.getShellType_i()));
		int count = self.getGoods(goodsId);
		if (count > 0) {
			// 道具不足，普通开火
			this.fire();
			return;
		}
		self.putGoods(goodsId, count - 1);
		param.setFireType(FireType.goods);
		param.setItemId(goodsId);

		// 广播使用金币弹
		this.brodcastFireMsg(FireType.goods, goodsId, 0, 0);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		// 计算伤害
		mapInstance.putHitQueue(param);
	}

	// 瞎放一炮
	private void fireEmpty() {
		int[] deviations = { -1, 1 };
		float offsetAngle = RandomUtil.randomInt(10);
		int index1 = RandomUtil.randomInt(deviations.length);
		int index2 = RandomUtil.randomInt(deviations.length);
		float offsetDirX = deviations[index1] * offsetAngle / 80;
		float offsetDirY = deviations[index2] * offsetAngle / 90;
		this.brodcastFireMsg(FireType.fire, 0, offsetDirX, offsetDirY);
	}

	// 广播开火消息
	private void brodcastFireMsg(FireType fireType, int stdItem, float skewX, float skewY) {
		STC_AI_FIRE_MSG fireMsg = STC_AI_FIRE_MSG.newBuilder().setSourceId(self.getId()).setTargetId(target.getId()).setFireType(fireType).setHadDodge(false).setStdItem(stdItem).setSkewX(skewX)
				.setSkewY(skewY).build();
		mapInstance.brodcastMsg(fireMsg);
		if (target.getId() == 0 && fireType == FireType.fire) {
			self.setMissCount(self.getMissCount() + 1);
		}
	}

	private void useGoods() {
		try {
			int rat = self.getAiAction().getGoodsRat();
			if (RandomUtil.randomInt(100) > rat || self.getBuffers().size() <= 0) {
				return;
			}
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
			int count = self.getGoods(goodsId);
			if (count <= 0) {
				return;
			}
			self.putGoods(goodsId, count - 1);

			GoodsWarTemplate template = GameContext.getGoodsApp().getGoodsWarTemplate(goodsId);
			// 广播使用效果
			STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder().setId(self.getId()).setGoodsId(goodsId).build();
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, efffectMsg.toByteArray());
			// 删除buff
			for (String strId : template.getDelBuff().split(Cat.comma)) {
				if (Util.isEmpty(strId)) {
					continue;
				}
				int delBuffId = Integer.parseInt(strId);
				GameContext.getBuffApp().remove(self, delBuffId);
			}
			// 添加buff
			GameContext.getBuffApp().putBuff(self, self, template.getAddBuff());
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	// 移动炮塔
	private void moveGun() {
		if (target == null) {
			return;
		}

		// 把炮台转向目标
		STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG.newBuilder().setId(self.getId()).setPosition(Coordinate3D.newBuilder().setPx(x).setPy(y).setPz(z)).setTarget(target.getId()).build();
		mapInstance.brodcastMsg(moveMsg);

		// 给转炮塔留时间
		self.setLastFireTime(System.currentTimeMillis());
	}

	// 开始填弹
	private void redloadShell() {
		if (target == null) {
			return;
		}

		// 切换开火状态
		this.setTarget(target);

	}

	// 寻找离自己最近两个敌人中血量最少的敌人
	private void searchEnemy() {

		this.target = null;

		// 寻找一个目标
		AbstractInstance firstTank = this.findNearEnemyOfFire(null);
		if (firstTank == null) {
			return;
		}
		if (isFire(firstTank)) {
			this.target = firstTank;
		}
	}

}
