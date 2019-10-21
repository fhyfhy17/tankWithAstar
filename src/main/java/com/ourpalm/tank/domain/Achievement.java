package com.ourpalm.tank.domain;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.QuestAdapter;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementState;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.STC_TRIGGER_TIP_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;

public class Achievement extends QuestAdapter  implements Comparable<Achievement>{
	private int id;		//成就id
	private int roleId; //角色id
	private int state;	//成就状态
	private int finishCount; //达成次数
	private transient int groupId;	//组id
	private transient int progress;	//成就进度
	
	@Override
	public int getState() {
		return state;
	}

	@Override
	public void setState(int state) {
		this.state = state;
	}
	
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public int getFinishCount() {
		return finishCount;
	}

	public void init() {
		this.finishCount = progress / questPhase.getLimit();
	}
	
	/** 更新进度 */
	@Override
	public void updateProgress() {
		//判断是否完成
		if(this.questPhase.hadFinish() && this.getState() == AchievementState.accept_VALUE){
			this.setState(AchievementState.reward_VALUE);
			sendRewardTip();
		}
		
		this.setProgress(questPhase.getProgress());
		
		int newFinishCount = this.progress / questPhase.getLimit();
		if(newFinishCount > finishCount) {
			finishCount = newFinishCount;
			sendTip();
		}
	}
	
	/** 是否已完成 */
	@Override
	public boolean hadComplete(){
		return false;
	}
	
	//发送红点提示
	public void sendRewardTip() {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_PROMPT_MSG promptMsg = STC_PROMPT_MSG.newBuilder().setPrompt(PROMPT.ACHIEVEMENT).build();
			connect.sendMsg(promptMsg);
		}
	}
	
	//成就面片提示
	private void sendTip() {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_TRIGGER_TIP_MSG.Builder builder = STC_TRIGGER_TIP_MSG.newBuilder();
			builder.setId(id);
			builder.setFinishCount(finishCount);
			
			connect.sendMsg(ACHIEVEMENT_MSG.CMD_TYPE.CMD_TYPE_ACHIEVEMENT_VALUE, ACHIEVEMENT_MSG.CMD_ID.STC_TRIGGER_TIP_VALUE, builder.build().toByteArray());
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getProgress() {
		return progress;
	}
	
	@Override
	public int compareTo(Achievement o) {
		if(this.state > o.getState()){
			return -1;
		}
		if(this.state < o.getState()){
			return 1;
		}
		if(this.id < o.getId()){
			return -1;
		}
		if(this.id > o.getId()){
			return 1;
		}
		return 0;
	}
	
}
