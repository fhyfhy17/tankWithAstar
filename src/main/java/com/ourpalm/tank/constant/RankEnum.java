package com.ourpalm.tank.constant;

public enum RankEnum {
	SeasonRankKey("ROLE_SEASON_RANK_KEY_", 0), // 赛季
	Kill("ROLE_KILL_RANK_KEY_", 1), // 击杀
	Help("ROLE_HELP_RANK_KEY_", 1), // 助攻榜
	Win("ROLE_WIN_RANK_KEY_", 1), // 胜场榜
	BattleNum("ROLE_BATTLENUM_RANK_KEY_", 2),// 战力
	;

	private String key;
	private int schedule;// 周期， 0为不走这个周期，1为每天 2 为每周

	RankEnum(String key, int schedule) {
		this.key = key;
		this.schedule = schedule;
	}

	public String getKey(int server) {
		if (server > 0)
			return key + server;
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getSchedule() {
		return schedule;
	}

	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}

}
