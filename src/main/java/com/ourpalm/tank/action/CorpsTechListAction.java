package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.TechnologyInfo;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_TECH_LST_MSG;
import com.ourpalm.tank.message.CORPS_MSG.TechnologyItem;
import com.ourpalm.tank.template.CorpsTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_TECH_LST_VALUE
)
public class CorpsTechListAction extends AbstractCorpAction implements Action<Object>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_TECH_LST_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);
		STC_CORPS_TECH_LST_MSG.Builder builder = STC_CORPS_TECH_LST_MSG.newBuilder();
		logger.debug("请求军团科技列表 corpsId = {}", corpsId);

		//捐赠冷却时间
		CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, roleId);
		builder.setCoolTime(corpsRole.donateCountdownTime());
		builder.setCoolState(GameContext.getCorpsApp().hadDonateCoolTime(corpsRole));

		logger.debug("捐献冷却时间={}, 冷却状态={}", corpsRole.donateCountdownTime(), builder.getCoolState());

		for(TechnologyInfo info : corps.getTechInfoMap().values()){
			TechnologyItem item = TechnologyItem.newBuilder()
					.setTechId(info.getTechId())
					.setTechCurExp(info.getCurExp())
					.setDevTime(info.devOverplusTime())
					.setState(GameContext.getCorpsApp().corpsTechState(corps, corpsRole, info.getTechId()))
					.build();
			logger.debug("科技ID = {}, exp = {}, 研发时间 = {}, state = {}"
					, info.getTechId(), info.getCurExp(), info.getDevOverTime(), item.getState());
			builder.addTechInfo(item);
		}
		CorpsTemplate template = GameContext.getCorpsApp().getInitTemplate();
		builder.setExpGoldRat(template.getExpGoldValue());
		builder.setTimeGoldRat(template.getTimeGoldValue());
		builder.setDonateTimeRat(template.getDonateTimeValue());
		builder.setMaxDonateCoolTime(template.getCoolTime() + template.getDonateOverstepTime());
		builder.setResult(Result.SUCCESS);

		return builder.build();
	}

}
