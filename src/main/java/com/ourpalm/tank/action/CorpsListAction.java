package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_LIST_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsItem;
import com.ourpalm.tank.message.CORPS_MSG.RecruitType;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_LIST_MSG;
import com.ourpalm.tank.template.CorpsTemplate;


@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_LIST_VALUE
)
public class CorpsListAction extends AbstractCorpAction implements Action<CTS_CORPS_LIST_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_LIST_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		int page = reqMsg.getPage();
		int areaId = connect.getAreaId();

		List<CorpsInfo> list = GameContext.getCorpsApp().getCorpsInfoList(areaId, page);
		CorpsTemplate template = GameContext.getCorpsApp().getInitTemplate();

		STC_CORPS_LIST_MSG.Builder builder = STC_CORPS_LIST_MSG.newBuilder();
		builder.setCreateDiamonds(template.getCreateDiamonds());

		for(CorpsInfo corps : list){
			// 已满的和拒绝的不显示
			if (isCorpsFull(corps) || corps.getRecruitType() == RecruitType.refuse_VALUE) {
				continue;
			}
			CorpsItem item = CorpsItem.newBuilder()
					.setActiveScore(corps.getActive())
					.setCurrPlayer(this.getCurrPlayer(corps.getId()))
					.setMaxPlayer(corps.getPlayerLimit())
					.setDec(corps.getDec())
					.setId(corps.getId())
					.setLeader(this.getRoleName(corps.getLeaderId()))
					.setLevel(corps.getLevel())
					.setName(corps.getName())
					.setRecruit(this.getRecruitType(corps, connect.getRoleId()))
					.build();
			builder.addCorps(item);
		}

		return builder.build();
	}

	private int getCurrPlayer(int corpsId){
		return GameContext.getCorpsApp().getCorpsRoleSize(corpsId);
	}

	private String getRoleName(int roleId){
		return GameContext.getUserApp().getRoleAccount(roleId).getRoleName();
	}

	//如果在此军团申请队列中，则显示审批中
	private RecruitType getRecruitType(CorpsInfo corps, int roleId){
		boolean exist = GameContext.getCorpsApp().hasExistCorpsApply(corps.getId(), roleId);
		return exist ? RecruitType.examine : RecruitType.valueOf(corps.getRecruitType());
	}

	private boolean isCorpsFull(CorpsInfo corps) {
		int capacity = corps.getPlayerLimit();
		int size = GameContext.getCorpsApp().getCorpsRoleSize(corps.getId());
		return size >= capacity;
	}
}
