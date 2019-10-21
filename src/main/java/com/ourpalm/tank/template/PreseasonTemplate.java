package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.KeySupport;

/**
 * 新手赛
 * 
 * @author fhy
 *
 */
public class PreseasonTemplate implements KeySupport<Integer> {
	// enemyTank ourTank flagAtk enemyKillo enemyHpLow beyond3

	private int id;
	private int mapId;
	private String enemyTank;
	private String ourTank;
	private String flagAtk;
	private String enemyKillo;
	// private String enemyHpLow;
	private int enemyHpLow;
	private int enemyHpLow2;
	private String beyond3;
	private String ourMoveToFlag;
	private String enemyMoveToFlag;
	private String ourFireToFlag;
	private String enemyFireToFlag;
	private String ourMoveAndFire;
	private String enemyMoveAndFire;
	private int cantEscapeInFlagTime;
	private int forceFireToFlag;
	

	private List<Integer> eTankList = new ArrayList<>();// 敌方坦克
	private List<Integer> oTankList = new ArrayList<>();// 我方坦克
	private List<int[]> flagAtkList = new ArrayList<>();// 攻击旗点内坦克时
	private List<int[]> eKilloList = new ArrayList<>();// 敌方AI杀人数大于我方
	// private List<int[]> eHpLowList = new ArrayList<>();// 敌方血量至20%时
	private List<int[]> beyond3List = new ArrayList<>();// 敌方杀人数大于我方3个
	private List<Integer> ourMoveToFlagList = new ArrayList<>();// 我方移动到旗子的概率列表（概率
																// 1-100，坦克 1-5）
	private List<Integer> enemyMoveToFlagList = new ArrayList<>();// 敌方移动到旗子的概率列表（概率
																	// 1-100，坦克
																	// 1-5）
	private List<Integer> ourFireToFlagList = new ArrayList<>();// 我方向旗子进攻的概率列表（概率
																// 1-100，坦克 1-5）
	private List<Integer> enemyFireToFlagList = new ArrayList<>();// 敌方向旗子进攻的概率列表（概率
																	// 1-100，坦克
																	// 1-5）
	private List<List<int[]>> ourMoveAndFireList = new ArrayList<>();// 我方对应坦克攻击前进的几率（概率
																		// 1-100，坦克
																		// 1-5）
	private List<List<int[]>> enemyMoveAndFireList = new ArrayList<>();// 敌方对应坦克攻击前进的几率（概率
																		// 1-100，坦克
																		// 1-5）

	public void init() {
		if (enemyTank != null && !this.enemyTank.isEmpty()) {
			String[] s = this.enemyTank.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				eTankList.add(Integer.parseInt(s[i]));
			}
		}
		if (ourTank != null && !this.ourTank.isEmpty()) {
			String[] s = this.ourTank.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				oTankList.add(Integer.parseInt(s[i]));
			}
		}
		if (flagAtk != null && !this.flagAtk.isEmpty()) {
			String[] s = this.flagAtk.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				String ss[] = s[i].split(Cat.comma);
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				flagAtkList.add(a);
			}
		}

		if (enemyKillo != null && !this.enemyKillo.isEmpty()) {
			String[] s = this.enemyKillo.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				String ss[] = s[i].split(Cat.comma);
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				eKilloList.add(a);
			}
		}
		// if (enemyHpLow!=null&&!this.enemyHpLow.isEmpty()) {
		// String[] s = this.enemyHpLow.split(Cat.semicolon);
		// for (int i = 0; i < s.length; i++) {
		// String ss[] = s[i].split(Cat.comma);
		// int[] a = new int[3];
		// a[0] = Integer.parseInt(ss[0]);
		// a[1] = Integer.parseInt(ss[1]);
		// a[2] = Integer.parseInt(ss[2]);
		// eHpLowList.add(a);
		// }
		// }
		if (beyond3 != null && !this.beyond3.isEmpty()) {
			String[] s = this.beyond3.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				String ss[] = s[i].split(Cat.comma);
				int[] a = new int[3];
				a[0] = Integer.parseInt(ss[0]);
				a[1] = Integer.parseInt(ss[1]);
				a[2] = Integer.parseInt(ss[2]);
				beyond3List.add(a);
			}
		}
		if (ourMoveToFlag != null && !this.ourMoveToFlag.isEmpty()) {
			String[] s = this.ourMoveToFlag.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				this.ourMoveToFlagList.add(Integer.parseInt(s[i]));
			}
		}
		if (enemyMoveToFlag != null && !this.enemyMoveToFlag.isEmpty()) {
			String[] s = this.enemyMoveToFlag.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				this.enemyMoveToFlagList.add(Integer.parseInt(s[i]));
			}
		}
		if (ourFireToFlag != null && !this.ourFireToFlag.isEmpty()) {
			String[] s = this.ourFireToFlag.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				this.ourFireToFlagList.add(Integer.parseInt(s[i]));
			}
		}
		if (enemyFireToFlag != null && !this.enemyFireToFlag.isEmpty()) {
			String[] s = this.enemyFireToFlag.split(Cat.semicolon);
			for (int i = 0; i < s.length; i++) {
				this.enemyFireToFlagList.add(Integer.parseInt(s[i]));
			}
		}
		if (ourMoveAndFire != null && !this.ourMoveAndFire.isEmpty()) {
			String[] s = this.ourMoveAndFire.split(Cat.slash);
			for (int i = 0; i < s.length; i++) {
				String ss[] = s[i].split(Cat.semicolon);
				List<int[]> moveAndFirePercent = new ArrayList<>();
				for (int j = 0; j < ss.length; j++) {
					String sss[] = ss[j].split(Cat.comma);
					int[] a = new int[2];
					a[0] = Integer.parseInt(sss[0]);
					a[1] = Integer.parseInt(sss[1]);
					moveAndFirePercent.add(a);
				}
				this.ourMoveAndFireList.add(moveAndFirePercent);
			}
		}
		if (enemyMoveAndFire != null && !this.enemyMoveAndFire.isEmpty()) {
			String[] s = this.enemyMoveAndFire.split(Cat.slash);
			for (int i = 0; i < s.length; i++) {
				String ss[] = s[i].split(Cat.semicolon);
				List<int[]> moveAndFirePercent = new ArrayList<>();
				for (int j = 0; j < ss.length; j++) {
					String sss[] = ss[j].split(Cat.comma);
					int[] a = new int[2];
					a[0] = Integer.parseInt(sss[0]);
					a[1] = Integer.parseInt(sss[1]);
					moveAndFirePercent.add(a);
				}
				this.enemyMoveAndFireList.add(moveAndFirePercent);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getEnemyTank() {
		return enemyTank;
	}

	public void setEnemyTank(String enemyTank) {
		this.enemyTank = enemyTank;
	}

	public String getOurTank() {
		return ourTank;
	}

	public void setOurTank(String ourTank) {
		this.ourTank = ourTank;
	}

	public String getFlagAtk() {
		return flagAtk;
	}

	public void setFlagAtk(String flagAtk) {
		this.flagAtk = flagAtk;
	}

	public String getEnemyKillo() {
		return enemyKillo;
	}

	public void setEnemyKillo(String enemyKillo) {
		this.enemyKillo = enemyKillo;
	}

	// public String getEnemyHpLow() {
	// return enemyHpLow;
	// }
	//
	// public void setEnemyHpLow(String enemyHpLow) {
	// this.enemyHpLow = enemyHpLow;
	// }

	public String getBeyond3() {
		return beyond3;
	}

	public int getEnemyHpLow() {
		return enemyHpLow;
	}

	public void setEnemyHpLow(int enemyHpLow) {
		this.enemyHpLow = enemyHpLow;
	}

	public void setBeyond3(String beyond3) {
		this.beyond3 = beyond3;
	}

	public List<Integer> geteTankList() {
		return eTankList;
	}

	public void seteTankList(List<Integer> eTankList) {
		this.eTankList = eTankList;
	}

	public List<Integer> getoTankList() {
		return oTankList;
	}

	public void setoTankList(List<Integer> oTankList) {
		this.oTankList = oTankList;
	}

	public List<int[]> getFlagAtkList() {
		return flagAtkList;
	}

	public void setFlagAtkList(List<int[]> flagAtkList) {
		this.flagAtkList = flagAtkList;
	}

	public List<int[]> geteKilloList() {
		return eKilloList;
	}

	public void seteKilloList(List<int[]> eKilloList) {
		this.eKilloList = eKilloList;
	}

	// public List<int[]> geteHpLowList() {
	// return eHpLowList;
	// }
	//
	// public void seteHpLowList(List<int[]> eHpLowList) {
	// this.eHpLowList = eHpLowList;
	// }
	//
	public List<int[]> getBeyond3List() {
		return beyond3List;
	}

	public void setBeyond3List(List<int[]> beyond3List) {
		this.beyond3List = beyond3List;
	}

	public List<Integer> getOurMoveToFlagList() {
		return ourMoveToFlagList;
	}

	public List<Integer> getEnemyMoveToFlagList() {
		return enemyMoveToFlagList;
	}

	public String getOurMoveToFlag() {
		return ourMoveToFlag;
	}

	public void setOurMoveToFlag(String ourMoveToFlag) {
		this.ourMoveToFlag = ourMoveToFlag;
	}

	public String getEnemyMoveToFlag() {
		return enemyMoveToFlag;
	}

	public void setEnemyMoveToFlag(String enemyMoveToFlag) {
		this.enemyMoveToFlag = enemyMoveToFlag;
	}

	public String getOurFireToFlag() {
		return ourFireToFlag;
	}

	public void setOurFireToFlag(String ourFireToFlag) {
		this.ourFireToFlag = ourFireToFlag;
	}

	public String getEnemyFireToFlag() {
		return enemyFireToFlag;
	}

	public void setEnemyFireToFlag(String enemyFireToFlag) {
		this.enemyFireToFlag = enemyFireToFlag;
	}

	public List<Integer> getOurFireToFlagList() {
		return ourFireToFlagList;
	}

	public void setOurFireToFlagList(List<Integer> ourFireToFlagList) {
		this.ourFireToFlagList = ourFireToFlagList;
	}

	public List<Integer> getEnemyFireToFlagList() {
		return enemyFireToFlagList;
	}

	public void setEnemyFireToFlagList(List<Integer> enemyFireToFlagList) {
		this.enemyFireToFlagList = enemyFireToFlagList;
	}

	public void setOurMoveToFlagList(List<Integer> ourMoveToFlagList) {
		this.ourMoveToFlagList = ourMoveToFlagList;
	}

	public void setEnemyMoveToFlagList(List<Integer> enemyMoveToFlagList) {
		this.enemyMoveToFlagList = enemyMoveToFlagList;
	}

	public String getOurMoveAndFire() {
		return ourMoveAndFire;
	}

	public void setOurMoveAndFire(String ourMoveAndFire) {
		this.ourMoveAndFire = ourMoveAndFire;
	}

	public String getEnemyMoveAndFire() {
		return enemyMoveAndFire;
	}

	public void setEnemyMoveAndFire(String enemyMoveAndFire) {
		this.enemyMoveAndFire = enemyMoveAndFire;
	}

	public List<List<int[]>> getOurMoveAndFireList() {
		return ourMoveAndFireList;
	}

	public void setOurMoveAndFireList(List<List<int[]>> ourMoveAndFireList) {
		this.ourMoveAndFireList = ourMoveAndFireList;
	}

	public List<List<int[]>> getEnemyMoveAndFireList() {
		return enemyMoveAndFireList;
	}

	public void setEnemyMoveAndFireList(List<List<int[]>> enemyMoveAndFireList) {
		this.enemyMoveAndFireList = enemyMoveAndFireList;
	}

	public int getCantEscapeInFlagTime() {
		return cantEscapeInFlagTime;
	}

	public void setCantEscapeInFlagTime(int cantEscapeInFlagTime) {
		this.cantEscapeInFlagTime = cantEscapeInFlagTime;
	}

	public int getEnemyHpLow2() {
		return enemyHpLow2;
	}

	public void setEnemyHpLow2(int enemyHpLow2) {
		this.enemyHpLow2 = enemyHpLow2;
	}

	public int getForceFireToFlag() {
		return forceFireToFlag;
	}

	public void setForceFireToFlag(int forceFireToFlag) {
		this.forceFireToFlag = forceFireToFlag;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}