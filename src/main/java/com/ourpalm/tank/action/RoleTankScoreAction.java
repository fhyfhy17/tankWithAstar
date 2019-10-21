package com.ourpalm.tank.action;

import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_TANK_SCORE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_TANK_SCORE_MSG;
import com.ourpalm.tank.template.TankCoefficientTemplate;
import com.ourpalm.tank.template.TankTemplate;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_TANK_SCORE_VALUE)
public class RoleTankScoreAction implements Action<CTS_TANK_SCORE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_TANK_SCORE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		int tankId = reqMsg.getTankId();
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		if (template == null) {
			return null;
		}

		Map<AttrType, Float> attrMap = GameContext.getTankApp().getAllTankMemberAttr(roleId, tankId);
		if (Util.isEmpty(attrMap)) {
			return null;
		}

		int memberScore = GameContext.getMemberApp().calcUseMemberStrength(roleId);
		float medialScoreRat = GameContext.getMemberApp().calcUseMedalStrengthRat(roleId);
		int tankScore = (int) template.getStrength();

		STC_TANK_SCORE_MSG.Builder builder = STC_TANK_SCORE_MSG.newBuilder();
		builder.setHp(attrMap.get(AttrType.n_hpMax).intValue());
		builder.setAtk(attrMap.get(AttrType.atk).intValue());
		builder.setFdef(attrMap.get(AttrType.fdef).intValue());
		builder.setIdef(attrMap.get(AttrType.idef).intValue());
		builder.setBdef(attrMap.get(AttrType.bdef).intValue());
		builder.setReload(attrMap.get(AttrType.danjia_time));
		builder.setMemberScore(memberScore);
		builder.setMedalScore(medialScoreRat);
		builder.setPartScore(0);
		builder.setEliteSocre(0);
		builder.setTankScore(tankScore);
		// TODO 后期要加
		int allScore = tankScore + memberScore;
		allScore = (int) ((float) allScore * (1 + medialScoreRat));

		// 触发活动
		GameContext.getActivityApp().battleScore(roleId, allScore);

		// 替换历史最高战斗力
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (allScore > role.getBattleScore()) {
			role.setBattleScore(allScore);
			GameContext.getUserApp().saveRoleAccount(role);
		}
		// 排行榜战斗力
		GameContext.getRankApp().rankAddBattleNum(roleId);
		role.setBattleScoreNow(allScore);
		return builder.build();
	}
}
