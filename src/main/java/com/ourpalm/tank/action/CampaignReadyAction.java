package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CAMP_MSG;
import com.ourpalm.tank.message.CAMP_MSG.STC_CAMP_READY_MSG;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

@Command(
	type = CAMP_MSG.CMD_TYPE.CMD_TYPE_CAMP_VALUE, 
	id = CAMP_MSG.CMD_ID.CTS_CAMP_READY_VALUE
)
public class CampaignReadyAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			throw new NullPointerException("坦克实例不存在...");
		}
		CampaignMapInstance mapInstance = (CampaignMapInstance)GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			throw new NullPointerException("地图实例不存在...");
		}
		//通知状态机，开始计时
		mapInstance.notify(NotifyState.enter, tank.getId());
		
		CampaignMapTemplate mapTemplate = mapInstance.getMapTemplate();
		STC_CAMP_READY_MSG.Builder buider = STC_CAMP_READY_MSG.newBuilder()
				.setReadyTime(mapTemplate.getReadyTime())
				.setOverTime(mapTemplate.getOverTime());
		
		return buider.build();
	}

}
