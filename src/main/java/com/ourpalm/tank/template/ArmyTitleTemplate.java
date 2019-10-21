package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class ArmyTitleTemplate implements KeySupport<Integer> {

	private int id_i;
	private String name_s;
	private int level_i;
	private int star_i;
	private int score; // 积分
	private int nextId; // 下一级军衔id
	private String matchDesc; // 军衔排位赛允许匹配说明
	private int teamMatchMin; // 军衔组队最小军衔id
	private int teamMatchMax; // 军衔组队最大军衔id
	private int rankMinTitle; // 军衔排位赛允许匹配最低军衔
	private float winMultiple; // 军衔排位赛获胜倍数率
	private float lostMultiple; // 军衔排位赛失败倍数率

	private String mailTitle; // 军衔赛季末邮件标题
	private String mailContext; // 邮件内容
	private int rewardId; // 赛季末邮件奖励
	private int nextSeasonId; // 下赛季起始军衔id

	private int gold; // 每日军衔奖励金币
	private int iron; // 每日军衔奖励银币
	private int honor; // 每日军衔奖励荣誉

	private int dropDay; // 掉分天数
	private int dropScore; // 掉分值

	private int titleGrade;// 军衔区段

	public int getTitleGrade() {
		return titleGrade;
	}

	public void setTitleGrade(int titleGrade) {
		this.titleGrade = titleGrade;
	}

	@Override
	public Integer getKey() {
		return this.id_i;
	}

	public int getId_i() {
		return id_i;
	}

	public void setId_i(int id_i) {
		this.id_i = id_i;
	}

	public String getName_s() {
		return name_s;
	}

	public void setName_s(String name_s) {
		this.name_s = name_s;
	}

	public int getLevel_i() {
		return level_i;
	}

	public void setLevel_i(int level_i) {
		this.level_i = level_i;
	}

	public int getStar_i() {
		return star_i;
	}

	public void setStar_i(int star_i) {
		this.star_i = star_i;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public int getNextSeasonId() {
		return nextSeasonId;
	}

	public void setNextSeasonId(int nextSeasonId) {
		this.nextSeasonId = nextSeasonId;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public String getMailTitle() {
		return mailTitle;
	}

	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
	}

	public String getMailContext() {
		return mailContext;
	}

	public void setMailContext(String mailContext) {
		this.mailContext = mailContext;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getMatchDesc() {
		return matchDesc;
	}

	public void setMatchDesc(String matchDesc) {
		this.matchDesc = matchDesc;
	}

	public int getTeamMatchMin() {
		return teamMatchMin;
	}

	public void setTeamMatchMin(int teamMatchMin) {
		this.teamMatchMin = teamMatchMin;
	}

	public int getTeamMatchMax() {
		return teamMatchMax;
	}

	public void setTeamMatchMax(int teamMatchMax) {
		this.teamMatchMax = teamMatchMax;
	}

	public int getRankMinTitle() {
		return rankMinTitle;
	}

	public void setRankMinTitle(int rankMinTitle) {
		this.rankMinTitle = rankMinTitle;
	}

	public int getDropDay() {
		return dropDay;
	}

	public void setDropDay(int dropDay) {
		this.dropDay = dropDay;
	}

	public int getDropScore() {
		return dropScore;
	}

	public void setDropScore(int dropScore) {
		this.dropScore = dropScore;
	}

	public float getWinMultiple() {
		return winMultiple;
	}

	public void setWinMultiple(float winMultiple) {
		this.winMultiple = winMultiple;
	}

	public float getLostMultiple() {
		return lostMultiple;
	}

	public void setLostMultiple(float lostMultiple) {
		this.lostMultiple = lostMultiple;
	}

	public int getNextId() {
		return nextId;
	}

	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
}
