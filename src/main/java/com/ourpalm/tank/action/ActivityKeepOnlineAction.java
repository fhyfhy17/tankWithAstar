package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._204_KeepOnlineLogic;
import com.ourpalm.tank.domain.ActivityKeepOnlineInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_KEEP_ONLINE_REWS_MSG;
import com.ourpalm.tank.template.ActivityKeepOnlineTemplate;

/**
 * 首充奖励
 * @author wangkun
 *
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_KEEP_ONLINE_REWS_VALUE
)
public class ActivityKeepOnlineAction implements Action<Object>{

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
		
		ActivityKeepOnlineInfo info = logic.getInfo(roleId);
		if(info == null) {
			return null;
		}
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int vipLevel = role.getVipLevel();
		
		ActivityKeepOnlineTemplate temp = logic.getKeepOnlineTemplate(vipLevel);
		if( temp == null ){
			return null;
		}
		
		STC_KEEP_ONLINE_REWS_MSG.Builder builder = STC_KEEP_ONLINE_REWS_MSG.newBuilder();
		
		float addValue = (float)temp.getAddValueBySec()/1000;
		builder.setAddValueBySec(addValue);
		builder.setUpValue(temp.getUpValue());
		builder.setVipLevel(vipLevel);
		builder.setLastDrawTime(info.getLastDrawTime());
		
		int curTime = (int)(System.currentTimeMillis()/1000);
		
		int totalSec = curTime - info.getLastDrawTime();
		if( totalSec < 0 ){
			return null;
		}
		
		int totalExp = (int)((float)temp.getAddValueBySec()/1000 * totalSec);
		if( totalExp > temp.getUpValue() ){
			totalExp = temp.getUpValue();
		}
		
		builder.setCurValue(totalExp);
		
		return builder.build();
	}

}
