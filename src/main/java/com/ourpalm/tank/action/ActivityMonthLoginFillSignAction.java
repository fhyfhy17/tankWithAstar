package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.monthlogin.ActivityMonthLoginApp;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_MONTHLOGIN_SIGN_MSG;
import com.ourpalm.tank.vo.result.ValueResult;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_MONTHLOGIN_FILL_SIGN_VALUE
)
public class ActivityMonthLoginFillSignAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		STC_ACTIVITY_MONTHLOGIN_SIGN_MSG.Builder builder = STC_ACTIVITY_MONTHLOGIN_SIGN_MSG.newBuilder();
		builder.setSuccess(false);
		builder.setFillSignCost(0);
		builder.setSignCount(0);
		builder.setCanFillSign(false);
		builder.setPointDayActive(false);
		
		ActivityMonthLoginApp app = GameContext.getActivityApp().getMonthLoginApp();
		ValueResult<ActivityMonthLoginInfo> result = app.fillSign(connect.getRoleId());
		if(result.isSuccess()) {
			ActivityMonthLoginInfo info = result.getValue();
			builder.setSignCount(info.getSignCount());
			builder.setSuccess(true);
			builder.setPointDayActive(info.isPointCanDraw());
			builder.setFillSignCost(app.getFillSignCost(info.getTodayFillSignCount()));
			builder.setCanFillSign(info.getFillSignCount() > 0);
		} else {
			builder.setInfo(result.getInfo());
		}
		
		
		return builder.build();
		
	}
	
	

}
