package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.KeySupport;

/**
 * 排行奖励
 * 
 * @author Administrator
 *
 */
public class RankRewardTemplate implements KeySupport<Integer> {

	private int id; // 活动id
	private String rank;
	private String killReward;
	private String battleNumReward;
	private String helpReward;
	private String winReward;

	private List<int[]> killitemList = new ArrayList<>();
	private List<int[]> battleNumitemList = new ArrayList<>();
	private List<int[]> helpitemList = new ArrayList<>();
	private List<int[]> winitemList = new ArrayList<>();
	private int[] rankArray = new int[2];

	public void init() {
		if (!"".equals(rank)) {
			String[] ss = rank.split(",");
			rankArray[0] = Integer.parseInt(ss[0]);
			rankArray[1] = Integer.parseInt(ss[1]);
		}
		if (!"".equals(killReward)) {
			String[] s = killReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				killitemList.add(a);
			}
		}
		if (!"".equals(battleNumReward)) {
			String[] s = battleNumReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				battleNumitemList.add(a);
			}
		}
		if (!"".equals(helpReward)) {
			String[] s = helpReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				helpitemList.add(a);
			}
		}
		if (!"".equals(winReward)) {
			String[] s = winReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				winitemList.add(a);
			}
		}
		if (!"".equals(winReward)) {
			String[] s = winReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				winitemList.add(a);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getKillReward() {
		return killReward;
	}

	public void setKillReward(String killReward) {
		this.killReward = killReward;
	}

	public String getBattleNumReward() {
		return battleNumReward;
	}

	public void setBattleNumReward(String battleNumReward) {
		this.battleNumReward = battleNumReward;
	}

	public String getHelpReward() {
		return helpReward;
	}

	public void setHelpReward(String helpReward) {
		this.helpReward = helpReward;
	}

	public String getWinReward() {
		return winReward;
	}

	public void setWinReward(String winReward) {
		this.winReward = winReward;
	}

	public int[] getRankArray() {
		return rankArray;
	}

	public void setRankArray(int[] rankArray) {
		this.rankArray = rankArray;
	}

	public List<int[]> getKillitemList() {
		return killitemList;
	}

	public void setKillitemList(List<int[]> killitemList) {
		this.killitemList = killitemList;
	}

	public List<int[]> getBattleNumitemList() {
		return battleNumitemList;
	}

	public void setBattleNumitemList(List<int[]> battleNumitemList) {
		this.battleNumitemList = battleNumitemList;
	}

	public List<int[]> getHelpitemList() {
		return helpitemList;
	}

	public void setHelpitemList(List<int[]> helpitemList) {
		this.helpitemList = helpitemList;
	}

	public List<int[]> getWinitemList() {
		return winitemList;
	}

	public void setWinitemList(List<int[]> winitemList) {
		this.winitemList = winitemList;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}
