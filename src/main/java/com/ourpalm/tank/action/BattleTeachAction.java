package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.STC_ENTER_TEACH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.template.BattleTeachTankTemplate;
import com.ourpalm.tank.template.BattleTeachTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

@Command(
		type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
		id = MATCH_MSG.CMD_ID.CTS_ENTER_TEACH_VALUE
	)
public class BattleTeachAction extends AbstractEnterMap implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		BattleTeachTemplate teachTemplate = GameContext.getMapApp().getTeachTemplate();
		
		STC_ENTER_TEACH_MSG.Builder builder = STC_ENTER_TEACH_MSG.newBuilder();
		builder.setMapId(teachTemplate.getId());
		
		// 坦克属性item
		TankItem tankItem = createTeachTank(teachTemplate.getTankId(), teachTemplate.getTankInstanceId(), 
				teachTemplate.getPlayerBirth(), teachTemplate.getTeam(), BattleApp.selfTankAttrs);
		
		builder.setTankItem(tankItem);						//玩家坦克
		builder.addAllOtherTanks(this.createOtherTeachTanks());	//敌方坦克
		builder.setMines(teachTemplate.getMineLocation());	//地雷位置
		builder.setTime(teachTemplate.getTime());			//比赛时常
		builder.setHoldTime(teachTemplate.getHoldTime());	//战旗时间
		
		return builder.build();
	}
	
	
	private List<TankItem> createOtherTeachTanks(){
		List<TankItem> list = new ArrayList<>();
		for(BattleTeachTankTemplate template : GameContext.getMapApp().getBattleTeachTankList()){
			TankItem item = createTeachTank(template.getTankId(), template.getId(), 
					template.getLocation(), template.getTeam(), BattleApp.otherTankAttr);
			list.add(item);
		}
		return list;
	}
	
	
	private TankItem createTeachTank(int tankId, int instanceId, Location birthLocation, int team, AttrType[] attrs){
		AbstractInstance tankInstance = GameContext.getTankApp().createTeachTank(instanceId, tankId);
		tankInstance.setRoleId(1);//避免识别成为机器人，客户端AI托管导致教学玩家无法移动
		tankInstance.setBirthLocation(birthLocation);
		tankInstance.setTeam(TEAM.valueOf(team));
		return GameContext.getBattleApp().buildTankItem(tankInstance, attrs);
	}
}

