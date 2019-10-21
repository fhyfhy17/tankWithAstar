package com.ourpalm.tank.action.inside;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.MTS_MATCH_RST_MSG;
import com.ourpalm.tank.vo.MatchParam;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
	id = SERV_MSG.CMD_ID.MTS_MATCH_RST_VALUE
)
public class MatchResultAction implements Action<MTS_MATCH_RST_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, MTS_MATCH_RST_MSG reqMsg) {
		logger.debug("接受到匹配处理，开始操作...");

		MatchParam param = new MatchParam();
		param.setMatchList(reqMsg.getTeamsList());
		param.setMapIndex(reqMsg.getMapIndex());
		param.setWarType(reqMsg.getType());
		param.setTeamMaxScore(reqMsg.getTeamMaxScore());
		param.setWeakTeam(reqMsg.getWeakTeam());
		param.setPreseason(reqMsg.getPreseason());

		GameContext.getMatchApp().matchNotifyEnter(param);

		return null;
	}


}
