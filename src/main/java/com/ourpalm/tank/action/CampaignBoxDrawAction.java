package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.DAOFactory;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleCamp;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CAMP_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CTS_CAMP_BOX_DRAW_MSG;
import com.ourpalm.tank.message.CAMP_MSG.STC_CAMP_BOX_DRAW_MSG;
import com.ourpalm.tank.template.CampaignBoxTemplate;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CAMP_MSG.CMD_TYPE.CMD_TYPE_CAMP_VALUE, 
	id = CAMP_MSG.CMD_ID.CTS_CAMP_BOX_DRAW_VALUE
)
public class CampaignBoxDrawAction implements Action<CTS_CAMP_BOX_DRAW_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CAMP_BOX_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		int warId = reqMsg.getId();
		int hard = reqMsg.getHard();
		
		STC_CAMP_BOX_DRAW_MSG.Builder builder = STC_CAMP_BOX_DRAW_MSG.newBuilder();
		//判断是否领取
		if(this.hadDrawCampBox(roleId, warId, hard)){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CAMP_BOX_DRAW);
			return builder.build();
		}
		
		//判断星级是否足够
		List<RoleCamp> roleCampList = DAOFactory.getRoleCampDao().getMuch(roleId, this.getHardCampId(warId, hard));
		if(Util.isEmpty(roleCampList)){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CAMP_STAR_NOT_ENOUGH);
			return builder.build();
		}
		//计算所获得的星级
		int star = 0;
		for(RoleCamp roleCamp : roleCampList){
			star += roleCamp.getStar();
		}
		CampaignBoxTemplate template = this.getCampaignBoxTemplate(warId, hard);
		if(template == null || star < template.getStar()){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CAMP_STAR_NOT_ENOUGH);
			return builder.build();
		}
		
		//给奖励，设置已领取
		GameContext.getGoodsApp().addGoods(roleId, template.getGoodsMap(), OutputType.campaignBoxDrawInc.getInfo());
		DAOFactory.getRoleCampDao().saveDrawCampBox(roleId, warId, hard);
		
		builder.setResult(Result.SUCCESS);
		return builder.build();
	}
	
	private CampaignBoxTemplate getCampaignBoxTemplate(int warId, int hard){
		for(CampaignBoxTemplate template : GameContext.getMapApp().getAllCampaignBoxTemplate(warId)){
			if(template.getHard() == hard){
				return template;
			}
		}
		return null;
	}

	private boolean hadDrawCampBox(int roleId, int warId, int hard){
		return DAOFactory.getRoleCampDao().hadCampBoxExist(roleId, warId, hard);
	}
	
	
	//战役下同一难度的关卡ID
	private List<String> getHardCampId(int warId, int hard){
		List<String> list = new ArrayList<>();
		for(CampaignMapTemplate template : GameContext.getMapApp().getAllCampaignMapTemplate(warId)){
			if(template.getHard() == hard){
				list.add(String.valueOf(template.getId()));
			}
		}
		return list;
	}
}
