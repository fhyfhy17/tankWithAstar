package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_MATCH_MSG;

/**
 * 申请匹配
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_MATCH_VALUE
)
public class BattleMatchAction extends AbstractEnterMap implements Action<CTS_MATCH_MSG>{

//	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_MATCH_MSG reqMsg) {
//		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
//		if(connect == null){
//			return null;
//		}
//		final int roleId = connect.getRoleId();
//		final int tankId = reqMsg.getTankId();
//		final int ioId = connect.getIoId();
//		final WAR_TYPE type = reqMsg.getWarType();
//		final String gateNode = connect.getGateName();
//		
//		
//		//判断是否可回到战场
//		if(this.backBattleWar(roleId, ioId, gateNode, AbstractEnterMap.RELINK_MATCH)){
//			return STC_MATCH_MSG.newBuilder()
//					.setResult(true)
//					.setInfo("")
//					.build();
//		}
//		
//		logger.debug("申请匹配roleId:{} tankId:{} type:{}", roleId, tankId, type);
//		
//		if(GameContext.getTankApp().getRoleTank(roleId, tankId) == null){
//			logger.debug("!!!!! 未拥有该坦克 tankId = {} roleId = {}", tankId, roleId);
//			return STC_MATCH_MSG.newBuilder()
//					.setResult(false)
//					.setInfo(Tips.TANK_NO_HAD)
//					.build();
//		}
//		//跨服匹配
//		this.svsMatch(connect, type, tankId);
//
//		return STC_MATCH_MSG.newBuilder()
//				.setResult(true)
//				.setInfo("")
//				.build();
		return null;
	}
	
}
