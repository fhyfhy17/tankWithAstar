package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_SEASON_RANK_LIST_MSG;
import com.ourpalm.tank.message.RANK_MSG.SeasonRankItem;

@Command(
	type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, 
	id = RANK_MSG.CMD_ID.CTS_SEASON_RANK_LIST_VALUE
)
public class SeasonRankListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());              
		if (connect == null) {
			return null;
		}
		
		STC_SEASON_RANK_LIST_MSG.Builder builder = STC_SEASON_RANK_LIST_MSG.newBuilder();
		
		boolean inRank = false;
		int rank = 1;
		List<Integer> rankRoleIds = GameContext.getSeasonRankApp().getRanks();
		for (Integer roleId : rankRoleIds) {
			SeasonRankItem item = buildRankItem(roleId, rank);
			if(item == null) {
				continue;
			}
			
			builder.addRanks(item);
			
			if(roleId == connect.getRoleId()) {
				builder.setRoleRank(item);
				inRank = true;
			}
			rank++;
		}
		
		if(!inRank) {
			SeasonRankItem item = buildRankItem(connect.getRoleId(), -1);
			builder.setRoleRank(item);
		}
		
		return builder.build();
	}
	
	
	private SeasonRankItem buildRankItem(int roleId, int rank) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null){
			return null;
		}
		
		SeasonRankItem.Builder itemBuilder = SeasonRankItem.newBuilder();
		itemBuilder.setName(account.getRoleName());
		itemBuilder.setTankId(account.getMainTankId());
		itemBuilder.setVip(account.getVipLevel());
		itemBuilder.setRank(rank);
		
		itemBuilder.setTitleId(account.getCurrTitleId());
		itemBuilder.setScore(account.getScore());
		itemBuilder.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(account.getRoleId()));
		itemBuilder.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(account.getRoleId()));
		
		return itemBuilder.build();
	}
}