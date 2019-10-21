package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_SELF_MSG;
import com.ourpalm.tank.message.RANK_MSG.RANK_SELF_INFO;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_SELF_MSG;

/**
 * 排行榜个人信息
 * 
 * @author fhy
 *
 */
@Command(type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, id = RANK_MSG.CMD_ID.CTS_RANK_SELF_VALUE)
public class RankSelfAction implements Action<CTS_RANK_SELF_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RANK_SELF_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		STC_RANK_SELF_MSG.Builder builder = STC_RANK_SELF_MSG.newBuilder();
		for (RankEnum rankEnum : RankEnum.values()) {
			RANK_SELF_INFO.Builder selfBuilder = RANK_SELF_INFO.newBuilder();
			selfBuilder.setType(rankEnum.ordinal());
			int rank = GameContext.getRankApp().getRoleRank(connect.getRoleId(), rankEnum == RankEnum.SeasonRankKey ? -1 : connect.getAreaId(), rankEnum);
			selfBuilder.setRank(rank > 0 ? rank + 1 : -1);
			selfBuilder.setScore(GameContext.getRankApp().getRoleScore(connect.getRoleId(), rankEnum == RankEnum.SeasonRankKey ? -1 : connect.getAreaId(), rankEnum));
			builder.addInfo(selfBuilder);
		}
		return builder.build();
	}

}
