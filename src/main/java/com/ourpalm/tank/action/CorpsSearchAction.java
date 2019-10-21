package com.ourpalm.tank.action;

import java.util.regex.Pattern;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_SEARCH_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsItem;
import com.ourpalm.tank.message.CORPS_MSG.RecruitType;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_LIST_MSG;
import com.ourpalm.tank.template.CorpsTemplate;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE, 
	id = CORPS_MSG.CMD_ID.CTS_CORPS_SEARCH_VALUE
)
public class CorpsSearchAction implements Action<CTS_CORPS_SEARCH_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_SEARCH_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		String key = reqMsg.getKey();
		
		CorpsTemplate template = GameContext.getCorpsApp().getInitTemplate();
		STC_CORPS_LIST_MSG.Builder builder = STC_CORPS_LIST_MSG.newBuilder();
		builder.setCreateDiamonds(template.getCreateDiamonds());
		
		CorpsInfo corpsInfo = null;
		//军团id搜索
		if(this.isNumeric(key)){
			corpsInfo = GameContext.getCorpsApp().getCorpsInfo(Integer.parseInt(key));
		} else {
			//军团名称搜索
			corpsInfo = GameContext.getCorpsApp().getCorpsInfo(connect.getAreaId(), key);
		}
		
		if(corpsInfo == null){
			return builder.build();
		}
		
		CorpsItem item = CorpsItem.newBuilder()
				.setActiveScore(corpsInfo.getActive())
				.setCurrPlayer(this.getCurrPlayer(corpsInfo.getId()))
				.setMaxPlayer(corpsInfo.getPlayerLimit())
				.setDec(corpsInfo.getDec())
				.setId(corpsInfo.getId())
				.setLeader(this.getRoleName(corpsInfo.getLeaderId()))
				.setLevel(corpsInfo.getLevel())
				.setName(corpsInfo.getName())
				.setRecruit(this.getRecruitType(corpsInfo, connect.getRoleId()))
				.build();
		builder.addCorps(item);
		
		return builder.build();
	}

	
	private boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
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
}
