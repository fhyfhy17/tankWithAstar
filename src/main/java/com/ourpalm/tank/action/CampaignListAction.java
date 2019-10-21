package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.DAOFactory;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleCamp;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CAMP_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CTS_CAMP_LIST_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CampItem;
import com.ourpalm.tank.message.CAMP_MSG.STC_CAMP_LIST_MSG;
import com.ourpalm.tank.template.CampaignMapTemplate;

/**
 * 战役列表
 * @author wangkun
 *
 */
@Command(
	type = CAMP_MSG.CMD_TYPE.CMD_TYPE_CAMP_VALUE, 
	id = CAMP_MSG.CMD_ID.CTS_CAMP_LIST_VALUE
)
public class CampaignListAction implements Action<CTS_CAMP_LIST_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CAMP_LIST_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		final int warId = reqMsg.getWarAreaId();
		List<CampaignMapTemplate> list = GameContext.getMapApp().getAllCampaignMapTemplate(warId);
		
		List<String> ids = new ArrayList<>();
		for(CampaignMapTemplate template : list){
			ids.add(String.valueOf(template.getId()));
		}
		
		STC_CAMP_LIST_MSG.Builder builder = STC_CAMP_LIST_MSG.newBuilder();
		List<RoleCamp> campList = DAOFactory.getRoleCampDao().getMuch(roleId, ids);
		
		for(RoleCamp roleCamp : campList){
			CampaignMapTemplate template = GameContext.getMapApp().getCampaignMapTemplate(roleCamp.getId());
			CampItem item = CampItem.newBuilder()
					.setGateId(template.getGateId())
					.setId(template.getId())
					.setStar(roleCamp.getStar())
					.build();
			builder.addCampItems(item);
		}
		return builder.build();
	}

}
