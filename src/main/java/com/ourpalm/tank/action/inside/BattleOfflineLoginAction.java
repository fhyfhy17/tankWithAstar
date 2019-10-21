package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.action.AbstractEnterMap;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_OFFLINE_RELOGIN_MSG;


@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.STS_OFFLINE_RELOGIN_VALUE
)
public class BattleOfflineLoginAction extends AbstractEnterMap implements Action<STS_OFFLINE_RELOGIN_MSG>{
	
	@Override
	public MessageLite execute(ActionContext context, STS_OFFLINE_RELOGIN_MSG reqMsg) {
		int roleId = reqMsg.getRoleId();
		int ioId = reqMsg.getIoId();
		String gateName = reqMsg.getGateNode();
//		int relinkType = reqMsg.getRelinkType();
		
		//修改消息来源为网关地址,否则此消息无法返回
		context.setFrom(gateName);
		//登录本机
		this.login(roleId, ioId, gateName);
		//切换连接
		this.changeRoleLink(ioId, gateName);
		
		//尝试进入战场
		boolean enterFlag = this.enterBattle(roleId);
		
		//进入失败告知客户端返回大厅
		if(!enterFlag){
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
			connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_BACK_WAR_VALUE, null);
		}
		
		//尝试进入失败并来自点击匹配
//		if(!enterFlag && relinkType == AbstractEnterMap.RELINK_MATCH){
//			//申请匹配
//			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
//			RoleBattle roleBattle = GameContext.getMatchApp().getLocalRoleBattle(roleId);
//			this.svsMatch(connect, WAR_TYPE.valueOf(roleBattle.getWarType()), roleBattle.getTankId());
//			
//			STC_MATCH_MSG resp = STC_MATCH_MSG.newBuilder()
//					.setResult(true)
//					.setInfo("")
//					.build();
//			connect.sendMsg(resp);
//		}
		
		return null;
	}
}
