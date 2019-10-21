package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_ROBOT_HIT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_MATCH_SCHEDULE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_SCHEDULE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.TankAiTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;


/**
 * 匹配进度action
 * @author fhy
 *
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_MATCH_SCHEDULE_VALUE
)
public class MatchScheduleAction implements Action<CTS_MATCH_SCHEDULE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MATCH_SCHEDULE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int schedule = reqMsg.getSchedule();
		int roleId = connect.getRoleId();
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if (tank == null) {
			return null;
		}
		STC_MATCH_SCHEDULE_MSG.Builder builder = STC_MATCH_SCHEDULE_MSG.newBuilder();
		GameContext.getMatchApp().matchSchedule(tank.getMapInstanceId(), roleId, schedule, builder);

		return null;
	}

}
