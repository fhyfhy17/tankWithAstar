package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.member.MemberApp;
import com.ourpalm.tank.app.member.MemberAppImpl;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleLottery;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.STC_LOTTERY_INFO_MSG;

@Command(type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE, id = MEMBER_MSG.CMD_ID.CTS_LOTTERY_INFO_VALUE)
public class MemberLotteryInfoAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		RoleLottery roleLottery = GameContext.getMemberApp().getRoleLottery(connect.getRoleId());
		STC_LOTTERY_INFO_MSG.Builder build = STC_LOTTERY_INFO_MSG.newBuilder();
		build.setDiamondsFreeCount(roleLottery.getDiamondsFreeCount());
		build.setDiamondsCount(MemberApp.MULTI_COUNT - roleLottery.getDiamondsCount());
		build.setIronFreeCount(roleLottery.getIronFreeCount());
		build.setIronCount(MemberApp.MULTI_COUNT - roleLottery.getIronCount());
		build.setFirstDiamondsMulti(roleLottery.isFirstMultDiamonds());
		long now = System.currentTimeMillis();
		long lastIron = roleLottery.getLastFreeIronTime();
		long freezeIronTime = MemberAppImpl.IRON_FREEZE_TIME * DateUtil.MIN;
		long nowMinusLastIron = now - lastIron;
		if (nowMinusLastIron < freezeIronTime) {
			build.setRestTimeIron((int) ((freezeIronTime - nowMinusLastIron) / DateUtil.SECOND));
		} else {
			build.setRestTimeIron(0);
		}

		long lastDiamond = roleLottery.getLastFreeDiamondTime();

		long freezeDiamondTime = MemberAppImpl.DIAMOND_FREEZE_TIME * DateUtil.MIN;
		long nowMinusLastDiamond = now - lastDiamond;
		if (nowMinusLastDiamond < freezeDiamondTime) {
			build.setRestTimeDiamond((int) ((freezeDiamondTime - nowMinusLastDiamond) / DateUtil.SECOND));
		} else {
			build.setRestTimeDiamond(0);
		}

		return build.build();
	}

}
