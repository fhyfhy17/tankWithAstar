package com.ourpalm.tank.domain;

import com.ourpalm.tank.constant.RankEnum;

public class RankRewardStateInfo {
	private RankEnum rankEnum;
	private int state;// 0 无 1可领2 已领
	private long receiveTime;// 获得时间
	private int rank;// 排名

	public RankEnum getRankEnum() {
		return rankEnum;
	}

	public void setRankEnum(RankEnum rankEnum) {
		this.rankEnum = rankEnum;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}
