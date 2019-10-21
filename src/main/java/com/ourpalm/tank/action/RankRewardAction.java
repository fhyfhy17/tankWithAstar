package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_REWARD_INFOS_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_SELF_MSG;
import com.ourpalm.tank.message.RANK_MSG.RANK_SELF_INFO;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_REWARD_INFOS_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_SELF_MSG;

/**
 * 排行榜个人信息
 * 
 * @author fhy
 *
 */
@Command(type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, id = RANK_MSG.CMD_ID.CTS_RANK_REWARD_INFOS_VALUE)
public class RankRewardAction implements Action<CTS_RANK_REWARD_INFOS_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RANK_REWARD_INFOS_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int type = reqMsg.getType();
		RankEnum rankEnum = null;
		try {
			rankEnum = RankEnum.values()[type];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		STC_RANK_REWARD_INFOS_MSG.Builder builder = STC_RANK_REWARD_INFOS_MSG.newBuilder();
		GameContext.getRankApp().getRewardInfo(builder, rankEnum);
		return builder.build();
	}

}
