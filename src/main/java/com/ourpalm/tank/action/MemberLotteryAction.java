package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.member.MemberApp;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_LOTTERY_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryItem;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryRewardType;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryType;
import com.ourpalm.tank.message.MEMBER_MSG.STC_LOTTERY_MSG;
import com.ourpalm.tank.vo.result.LotteryResult;
import com.ourpalm.tank.vo.result.Reward;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_LOTTERY_VALUE
)
public class MemberLotteryAction implements Action<CTS_LOTTERY_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_LOTTERY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		LotteryType type = reqMsg.getType();
		LotteryResult result = GameContext.getMemberApp().lottery(roleId, type);
		
		STC_LOTTERY_MSG.Builder builder = STC_LOTTERY_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		builder.setType(reqMsg.getType());
		for (Reward reward : result.getReward()) {
			LotteryItem.Builder item = LotteryItem.newBuilder();
			item.setType(LotteryRewardType.valueOf(reward.getType()));
			item.setId(reward.getId());
			item.setNum(reward.getNum());
			item.setHadExist(reward.isHadExist());
			builder.addItems(item);
		}
		
		//调用抽奖任务
		if(result.isSuccess()){
			int count = LotteryType.diamondsOnce.equals(type) || LotteryType.ironOnce.equals(type)? 1 : MemberApp.MULTI_COUNT;
			GameContext.getQuestTriggerApp().memberLottery(roleId, count);
		}
		return builder.build();
	}

}
