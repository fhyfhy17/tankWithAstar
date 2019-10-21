package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_ENTER_CAMPAIGN_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_ENTER_CAMPAIGN_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.result.CampEnterResult;
import com.ourpalm.tank.vo.result.Result;


/**
 * 进入战役
 * @author wangkun
 *
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_ENTER_CAMPAIGN_VALUE
)
public class CampaignEnterAction extends AbstractEnterMap implements Action<CTS_ENTER_CAMPAIGN_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_ENTER_CAMPAIGN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int id = reqMsg.getId();
		int roleId = connect.getRoleId();
		
		STC_ENTER_CAMPAIGN_MSG.Builder builder = STC_ENTER_CAMPAIGN_MSG.newBuilder();
		
		CampEnterResult result = GameContext.getMapApp().enterCampaign(id, roleId);
		if(! result.isSuccess()){
			builder.setResult(result.getResult());
			builder.setInfo(result.getInfo());
			return builder.build();
		}
		
		CampaignMapInstance mapInstance = result.getMapInstance();
		AbstractInstance tank = result.getSelfTank();
		
		// 自身坦克属性
		TankItem tankItem = GameContext.getBattleApp().buildTankItem(tank, BattleApp.selfTankAttrs);
		
		builder.setResult(Result.SUCCESS);
		builder.setMapId(mapInstance.getMapId());
		builder.setTankItem(tankItem);
		builder.addAllOtherTanks(this.getOtherTanks(mapInstance, tank.getRoleId()));
		//战场道具
		builder.addAllGoodsItem(this.getGoodsItem(tank));
		//技能列表
		builder.addAllSkillIds(this.getSkillItem(tank));
		
		builder.addAllBuildItem(this.getBuildItems(mapInstance));
		
		return builder.build();
	}
}
