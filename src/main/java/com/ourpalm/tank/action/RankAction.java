package com.ourpalm.tank.action;

import java.util.HashMap;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.rank.RankAppImpl;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.RankRewardStateInfo;
import com.ourpalm.tank.domain.RankRoleIdAndScore;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_INFO_MSG;
import com.ourpalm.tank.message.RANK_MSG.RankItem;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_INFO_MSG;

@Command(type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, id = RANK_MSG.CMD_ID.CTS_RANK_INFO_VALUE)
public class RankAction implements Action<CTS_RANK_INFO_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RANK_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int type = reqMsg.getType();
		int page = reqMsg.getPage();
		RankEnum rankEnum = RankEnum.values()[type];
		int start = (page - 1) * RankAppImpl.RANK_PAGE_SHOW_NUM;
		int end = start + RankAppImpl.RANK_PAGE_SHOW_NUM - 1;
		// 如果是军衔排位，最大为50条
		if (type == 0) {
			if (start > GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount() - 1) {
				start = GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount() - 1;
			}
			if (end > GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount() - 1) {
				end = GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount() - 1;
			}
		}
		List<RankRoleIdAndScore> list = GameContext.getRankApp().getRanks(start, end, rankEnum == RankEnum.SeasonRankKey ? -1 : connect.getAreaId(), rankEnum);
		STC_RANK_INFO_MSG.Builder builder = STC_RANK_INFO_MSG.newBuilder();

		for (RankRoleIdAndScore rankRoleIdAndScore : list) {
			start++;
			RankItem.Builder builderRankItem = RankItem.newBuilder();
			RoleAccount role = GameContext.getUserApp().getRoleAccount(rankRoleIdAndScore.getRoleId());
			if (role == null)
				continue;

			builderRankItem.setName(role.getRoleName());
			builderRankItem.setScore(rankRoleIdAndScore.getScore());
			builderRankItem.setRank(start);
			builderRankItem.setTitleId(role.getCurrTitleId());
			builderRankItem.setVip(role.getVipLevel());
			builderRankItem.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(role.getRoleId()));
			builderRankItem.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(role.getRoleId()));
			builderRankItem.setRoleId(role.getRoleId());
//			System.out.println(builderRankItem.getName());
//			System.out.println(builderRankItem.getScore());
//			System.out.println(builderRankItem.getRank());
//			System.out.println("=============================");
			builder.addRanks(builderRankItem);
		}
//		RankItem.Builder builderRankItemRole = RankItem.newBuilder();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
//		int roleRank = GameContext.getRankApp().getRoleRank(role.getRoleId(), rankEnum == RankEnum.SeasonRankKey ? -1 : role.getAreaId(), rankEnum);
//		builderRankItemRole.setName(role.getRoleName());
//		builderRankItemRole.setScore(GameContext.getRankApp().getRoleScore(role.getRoleId(), rankEnum == RankEnum.SeasonRankKey ? -1 : role.getAreaId(), rankEnum));
//		builderRankItemRole.setRank(roleRank+1);
//		builderRankItemRole.setTitleId(role.getCurrTitleId());
//		builderRankItemRole.setVip(role.getVipLevel());
//		builderRankItemRole.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(role.getRoleId()));
//		builderRankItemRole.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(role.getRoleId()));
//		builder.setRoleRank(builderRankItemRole);

		HashMap<RankEnum, RankRewardStateInfo> stateMap = role.getRankReward();
		RankRewardStateInfo rankRewardStateInfo = stateMap.get(rankEnum);
		builder.setCanReward(String.valueOf(0));
		if (rankRewardStateInfo != null) {
			builder.setCanReward(String.valueOf(GameContext.getRankApp().getRewardState(role.getRoleId(), rankEnum)));
		}
		return builder.build();
	}

}
