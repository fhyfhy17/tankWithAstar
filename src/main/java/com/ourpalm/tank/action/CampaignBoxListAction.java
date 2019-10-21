package com.ourpalm.tank.action;

import java.util.List;
import java.util.Map.Entry;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.DAOFactory;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CAMP_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CTS_CAMP_BOX_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CampBoxItem;
import com.ourpalm.tank.message.CAMP_MSG.CampGoodsItem;
import com.ourpalm.tank.message.CAMP_MSG.STC_CAMP_BOX_MSG;
import com.ourpalm.tank.template.CampaignBoxTemplate;

@Command(
	type = CAMP_MSG.CMD_TYPE.CMD_TYPE_CAMP_VALUE, 
	id = CAMP_MSG.CMD_ID.CTS_CAMP_BOX_VALUE
)
public class CampaignBoxListAction implements Action<CTS_CAMP_BOX_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CAMP_BOX_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		int warId = reqMsg.getId();
		List<CampaignBoxTemplate> list = GameContext.getMapApp().getAllCampaignBoxTemplate(warId);
		if(Util.isEmpty(list)){
			return null;
		}
		
		STC_CAMP_BOX_MSG.Builder builder = STC_CAMP_BOX_MSG.newBuilder();
		for(CampaignBoxTemplate template : list){
			CampBoxItem.Builder item = CampBoxItem.newBuilder();
			item.setId(template.getWarId());
			item.setHard(template.getHard());
			item.setStar(template.getStar());
			item.setHadDraw(DAOFactory.getRoleCampDao().hadCampBoxExist(roleId, warId, template.getHard()));
			for(Entry<Integer, Integer> entry : template.getGoodsMap().entrySet()){
				CampGoodsItem goodsItem = CampGoodsItem.newBuilder()
						.setGoodsId(entry.getKey())
						.setNum(entry.getValue())
						.build();
				item.addGoodsItem(goodsItem);
			}
			builder.addBoxItem(item);
		}
		
		return builder.build();
	}

}
