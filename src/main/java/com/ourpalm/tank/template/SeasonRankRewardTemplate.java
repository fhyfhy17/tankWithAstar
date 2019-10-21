package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.KeySupport;

/**
 * 军衔排行奖励
 * 
 * @author Administrator
 *
 */
public class SeasonRankRewardTemplate implements KeySupport<Integer> {

	private int id; // id
	private String seasonRank;
	private String seasonEveryReward;
	private String seasonFinalReward;

	private List<int[]> seasonEveryList = new ArrayList<>();
	private List<int[]> seasonFinalList = new ArrayList<>();
	private int[] seasonRankArray = new int[2];

	public void init() {
		if (!"".equals(seasonRank)) {
			String[] ss = seasonRank.split(",");
			seasonRankArray[0] = Integer.parseInt(ss[0]);
			seasonRankArray[1] = Integer.parseInt(ss[1]);
		}
		if (!"".equals(seasonEveryReward)) {
			String[] s = seasonEveryReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				seasonEveryList.add(a);
			}
		}
		if (!"".equals(seasonFinalReward)) {
			String[] s = seasonFinalReward.split(";");

			for (int i = 0; i < s.length; i++) {
				String[] ss = s[i].split(",");
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				seasonFinalList.add(a);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSeasonEveryReward() {
		return seasonEveryReward;
	}

	public void setSeasonEveryReward(String seasonEveryReward) {
		this.seasonEveryReward = seasonEveryReward;
	}

	public List<int[]> getSeasonEveryList() {
		return seasonEveryList;
	}

	public void setSeasonEveryList(List<int[]> seasonEveryList) {
		this.seasonEveryList = seasonEveryList;
	}

	public String getSeasonRank() {
		return seasonRank;
	}

	public void setSeasonRank(String seasonRank) {
		this.seasonRank = seasonRank;
	}

	public String getSeasonFinalReward() {
		return seasonFinalReward;
	}

	public void setSeasonFinalReward(String seasonFinalReward) {
		this.seasonFinalReward = seasonFinalReward;
	}

	public List<int[]> getSeasonFinalList() {
		return seasonFinalList;
	}

	public void setSeasonFinalList(List<int[]> seasonFinalList) {
		this.seasonFinalList = seasonFinalList;
	}

	public int[] getSeasonRankArray() {
		return seasonRankArray;
	}

	public void setSeasonRankArray(int[] seasonRankArray) {
		this.seasonRankArray = seasonRankArray;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}
