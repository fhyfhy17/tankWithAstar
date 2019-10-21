package com.ourpalm.tank.domain;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.QuestAdapter;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;


public class Quest extends QuestAdapter implements Comparable<Quest>{
	
	private int id;
	private int state;
	private int roleId;
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
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getProgress(){
		return this.progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getLimit(){
		return this.questPhase.getLimit();
	}
	
	@Override
	public int compareTo(Quest o) {
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
	public void updateProgress() {
		//判断是否完成
		if(this.questPhase.hadFinish() && this.getState() == QuestState.accept_VALUE){
			this.setState(QuestState.reward_VALUE);
			sendRewardTip();
		}
		this.setProgress(questPhase.getProgress());
	}
	
	public void sendRewardTip() {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_PROMPT_MSG promptMsg = STC_PROMPT_MSG.newBuilder().setPrompt(PROMPT.ACTIVE).build();
			connect.sendMsg(promptMsg);
		}
	}
}
