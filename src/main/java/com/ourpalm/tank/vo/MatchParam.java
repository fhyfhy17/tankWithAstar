package com.ourpalm.tank.vo;

import java.util.List;

import com.ourpalm.tank.message.SERV_MSG.MatchTeam;

public class MatchParam {

	private List<MatchTeam> matchList; // 匹配列表
	private int warType; // 战场模式
	private int mapIndex; // 地图索引ID
	private int teamMaxScore; // 队伍最大匹配分
	private String weakTeam; // 弱队
	private int preseason; // 新手赛

	public List<MatchTeam> getMatchList() {
		return matchList;
	}

	public void setMatchList(List<MatchTeam> matchList) {
		this.matchList = matchList;
	}

	public int getWarType() {
		return warType;
	}

	public void setWarType(int warType) {
		this.warType = warType;
	}

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public int getTeamMaxScore() {
		return teamMaxScore;
	}

	public void setTeamMaxScore(int teamMaxScore) {
		this.teamMaxScore = teamMaxScore;
	}

	public String getWeakTeam() {
		return weakTeam;
	}

	public void setWeakTeam(String weakTeam) {
		this.weakTeam = weakTeam;
	}

	public int getPreseason() {
		return preseason;
	}

	public void setPreseason(int preseason) {
		this.preseason = preseason;
	}

}
