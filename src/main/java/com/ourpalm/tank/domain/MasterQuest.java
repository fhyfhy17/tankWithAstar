package com.ourpalm.tank.domain;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.QuestAdapter;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;

public class MasterQuest extends QuestAdapter implements Comparable<MasterQuest>{

	private int id;
	private int roleId;
	private int state;
	private int progress;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getProgress(){
		return this.progress;
	}
	public int getLimit(){
		return this.questPhase.getLimit();
	}
	
	/** 更新进度 */
	public void updateProgress() {
		//判断是否完成
		if(this.questPhase.hadFinish() && this.getState() == QuestState.accept_VALUE){
			this.setState(QuestState.reward_VALUE);
			sendRewardTip();
		}
		this.setProgress(questPhase.getProgress());
	}
	
	//发送红点提示
	public void sendRewardTip() {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_PROMPT_MSG promptMsg = STC_PROMPT_MSG.newBuilder().setPrompt(PROMPT.MASTER_QUEST).build();
			connect.sendMsg(promptMsg);
		}
	}
	
	@Override
	public int compareTo(MasterQuest o) {
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
	
	@Override
	public int getState() {
		return state;
	}

	@Override
	public void setState(int state) {
		this.state = state;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

}
