package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.OTS_SYNC_MATCH_INFO_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.OTS_SYNC_MATCH_INFO_VALUE
)
public class SyncMatchInfoAction implements Action<OTS_SYNC_MATCH_INFO_MSG>{

	@Override
	public MessageLite execute(ActionContext context, OTS_SYNC_MATCH_INFO_MSG reqMsg) {
//		String matchInfoStr = reqMsg.getMatchInfo();
//		if(matchInfoStr == null)
//			return null;
//		
//		SyncMatchBody body = JSON.parseObject(matchInfoStr, SyncMatchBody.class);
//		
//		
//		STO_SYNC_MATCH_INFO_MSG.Builder builder = STO_SYNC_MATCH_INFO_MSG.newBuilder();
//		builder.setCode(1000);
//		
//		Message m = new Message();
//		m.setCmdType((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
//		m.setCmdId((byte)SERV_MSG.CMD_ID.STO_SYNC_MATCH_INFO_VALUE);
//		m.setFromNode(GameContext.getLocalNodeName());
//		m.setData(builder.build().toByteArray());
//		
//		String fromNode = context.getFrom();
//		RemoteNode node = GameContext.getOutsideManagerApp().getRemoteNode(fromNode);
//		if(node != null){
//			node.sendReqMsg(m);
//		}
//		
//		LogCore.runtime.info("收到匹配服务器申请返回, id = {}, name={}.", body.getMatch_id(), body.getMatch_name());
		
		return null;
	}

}
