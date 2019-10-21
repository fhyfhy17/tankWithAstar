package com.ourpalm.tank.vo;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.app.battle.HitParamAttrRecover;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.util.SysConfig;

public enum TankPreseasonDeal {
	INSTANCE,;

	/**
	 * 取得比赛的新手赛模板
	 * 
	 * @return
	 */
	public PreseasonTemplate gePreseasonTemplate(AbstractInstance self) {
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		if (mapInstance == null)
			return null;
		return mapInstance.getPreseasonTemplate();
	}

	/**
	 * 
	 * 
	 * @param param
	 * @param targetTank
	 */
	public void preseasonCalc(HitParam param, AbstractInstance self, AbstractInstance targetTank) {
		PreseasonTemplate preseasonTemplate = gePreseasonTemplate(self);
		if (preseasonTemplate == null) {
			return;
		}
		HitParamAttrRecover h = null;
		// 在前5关 打圈里的敌方 攻击力加成
		if (isInFlag(targetTank)) {
			if (self.getTeam() == TEAM.BLUE && targetTank.getTeam() == TEAM.RED) {
				if (!preseasonTemplate.getFlagAtkList().isEmpty()) {
					// System.err.println("我方原攻击力 "+
					// this.get(AttrType.valueOf(10)));

					h = new HitParamAttrRecover();
					for (int[] is : preseasonTemplate.getFlagAtkList()) {
						int camp = is[0];
						AttrType attr = AttrType.valueOf(is[1]);
						if (camp == 1) {
							h.getOldSelfAttrMap().put(attr, self.get(attr));
							self.changeAttr(attr, self.get(attr) + self.get(attr) * is[2] * 1.0f / 100);
						} else {
							h.getOldTargetAttrMap().put(attr, self.get(attr));
							targetTank.changeAttr(attr, targetTank.get(attr) + targetTank.get(attr) * is[2] * 1.0f / 100);
						}
					}
					// System.err.println("我方改变后的攻击力 "+
					// this.get(AttrType.valueOf(10)));
				}
			}
		}
		if (preseasonTemplate.getEnemyHpLow() != 0 && self.getRoleId() < 0) {
			// 敌方AI血量至20%，已方AI跳弹率提高
			if (targetTank.get(AttrType.n_hpMax) * SysConfig.get(16) * 1.0f / 100 > targetTank.get(AttrType.hp) && !isRedBeyond(self)) {
				if (targetTank.getTeam() == TEAM.RED) {
					param.setHadDodge(Math.random() < preseasonTemplate.getEnemyHpLow() * 1.0f / 100);
				}
				if (targetTank.getTeam() == TEAM.BLUE && targetTank.getRoleId() > 0) {
					param.setHadDodge(Math.random() < preseasonTemplate.getEnemyHpLow2() * 1.0f / 100);
				}
				// if (h == null) {
				// h = new HitParamAttrRecover();
				// }
				// System.err.println("我方原跳蛋7 "+ this.get(AttrType.valueOf(7)));
				// System.err.println("我方原跳蛋8 "+ this.get(AttrType.valueOf(8)));
				// System.err.println("我方原跳蛋9 "+ this.get(AttrType.valueOf(9)));
				// for (int[] is : preseasonTemplate.geteHpLowList()) {
				// int camp = is[0];
				// AttrType attr = AttrType.valueOf(is[1]);
				// if (camp == 1) {
				// h.getOldSelfAttrMap().put(attr, self.get(attr));
				// self.changeAttr(attr, self.get(attr) + self.get(attr) * is[2]
				// * 1.0f / 100);
				// } else {
				// h.getOldTargetAttrMap().put(attr, self.get(attr));
				// targetTank.changeAttr(attr, targetTank.get(attr) +
				// self.get(attr) * is[2] * 1.0f / 100);
				// }
				// }
				// System.err.println("我方变后跳蛋7 "+
				// this.get(AttrType.valueOf(7)));
				// System.err.println("我方变后跳蛋8 "+
				// this.get(AttrType.valueOf(8)));
				// System.err.println("我方变后跳蛋9 "+
				// this.get(AttrType.valueOf(9)));
			}
		}
		if (h != null) {
			param.setCallBack(h);
		}
	}

	public boolean isRedBeyond(AbstractInstance self) {
		int redAllKill = 0;
		int blueAllKill = 0;
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		for (AbstractInstance ist : mapInstance.getAllTank()) {
			if (ist.getTeam() == TEAM.RED) {
				redAllKill += ist.getKillCount();
			} else {
				blueAllKill += ist.getKillCount();
			}
		}
		return redAllKill > blueAllKill;
	}

	// 判断是否在旗子圈里
	public boolean isInFlag(AbstractInstance target) {
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(target.getMapInstanceId());
		if (mapInstance != null && mapInstance instanceof SportMapInstance && mapInstance.getWarType() == WAR_TYPE.BATTLE_VALUE) {
			SportMapInstance mapInstance2 = (SportMapInstance) mapInstance;
			MapTemplate mapTem = mapInstance2.getTemplate();

			float x = Math.abs(target.getX() - mapTem.getX());
			float z = Math.abs(target.getZ() - mapTem.getZ());

			int range = (int) Math.sqrt(x * x + z * z);
			if (mapTem.getRadius() > range) {
				return true;
			}
		}
		return false;
	}
}
