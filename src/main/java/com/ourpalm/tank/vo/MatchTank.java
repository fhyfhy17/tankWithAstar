package com.ourpalm.tank.vo;

public class MatchTank implements Comparable<MatchTank> {

	private int roleId;
	private String name; // 角色名称
	private int tankId; // 坦克id
	private int battleScore; // 战斗力
	private boolean hadElite; // 是否精英坦克
	private boolean hadReady; // 是否已准备
	private int score; // 匹配积分
	private int tankLevel; // 坦克等级
	private int friendId;//好友ID（为战斗加成用）
	private int corpMemberId;//军团成员ID（为战斗加成用）

	@Override
	public int compareTo(MatchTank matchTank) {
		return matchTank.getScore() - this.score;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTankId() {
		return tankId;
	}

	public void setTankId(int tankId) {
		this.tankId = tankId;
	}

	public int getBattleScore() {
		return battleScore;
	}

	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}

	public boolean isHadElite() {
		return hadElite;
	}

	public void setHadElite(boolean hadElite) {
		this.hadElite = hadElite;
	}

	public boolean isHadReady() {
		return hadReady;
	}

	public void setHadReady(boolean hadReady) {
		this.hadReady = hadReady;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTankLevel() {
		return tankLevel;
	}

	public void setTankLevel(int tankLevel) {
		this.tankLevel = tankLevel;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getCorpMemberId() {
		return corpMemberId;
	}

	public void setCorpMemberId(int corpMemberId) {
		this.corpMemberId = corpMemberId;
	}

}
