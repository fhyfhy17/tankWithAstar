package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._204_KeepOnlineLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_KEEP_ONLINE_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 首充奖励
 * @author wangkun
 *
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_KEEP_ONLINE_DRAW_VALUE
)
public class ActivityKeepOnlineDrawAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		
		_204_KeepOnlineLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.KeepOnline);
		if(logic == null) {
			return null;
		}
		
		Result result = logic.reward(roleId);
		
		STC_KEEP_ONLINE_DRAW_MSG.Builder builder = STC_KEEP_ONLINE_DRAW_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		
		if(result.isSuccess()) {
			List<RewardItem> rewardResult = new ArrayList<>();
			RewardItem.Builder itemBuilder = RewardItem.newBuilder();
			itemBuilder.setCount(Integer.parseInt(result.getInfo()));
			itemBuilder.setId(0);
			itemBuilder.setType(RewardType.TANKEXP);
			rewardResult.add(itemBuilder.build());
			builder.addAllRewards(rewardResult);
		}
		
		return builder.build();
	}

}
