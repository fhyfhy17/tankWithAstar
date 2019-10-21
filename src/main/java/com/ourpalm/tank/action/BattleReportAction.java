package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_WAR_REPORT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.WarReportItem;
import com.ourpalm.tank.message.BATTLE_MSG.WarResultItem;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

/**
 * 获取战报信息
 *
 */
@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
	id = BATTLE_MSG.CMD_ID.CTS_WAR_REPORT_VALUE
)
public class BattleReportAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			return null;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		STC_WAR_REPORT_MSG.Builder builder = STC_WAR_REPORT_MSG.newBuilder();
		WarResultItem.Builder item = WarResultItem.newBuilder();
		item.setMvpRoleId(0);
		item.setMvpCount(0);
		item.setTime(mapInstance.durationTime());
		item.setResult(0);
		item.setBlue(mapInstance.getBlueKillNum());
		item.setRed(mapInstance.getRedKillNum());
		item.setFlagWin(false);
		builder.setResult(item);
		
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if(ist == null){
				continue;
			}
			WarReportItem.Builder reportItem = WarReportItem.newBuilder();
			reportItem.setId(ist.getId());
			reportItem.setTankId(ist.getTemplateId());
			reportItem.setElite(ist.isEliteTank() ? 1 : 0);
			reportItem.setRoleName(ist.getRoleName());
			reportItem.setTeam(ist.getTeam().getNumber());
			reportItem.setKill(ist.getKillCount());
			reportItem.setDeath(ist.getDeathCount());
			reportItem.setHelpKill(ist.getHelpKill());
			reportItem.setAidKill(ist.getAidKill());
			reportItem.setMvpFlag(0);
			reportItem.setMvpScore(ist.getMvpScore());
			reportItem.setLevel(ist.getLevel());
			reportItem.setTitleId(ist.getTitleId());
			reportItem.setBattleScore(ist.getBattleScore());
			reportItem.setRoleId(ist.getRoleId());
			reportItem.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(ist.getRoleId()));
			reportItem.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(ist.getRoleId()));
			builder.addReports(reportItem);
		}
		
		return builder.build();
	}

}
