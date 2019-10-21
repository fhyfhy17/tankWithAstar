package com.ourpalm.tank.app.match.ga;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.app.match.Team;
import com.ourpalm.tank.app.match.TeamItem;
import com.ourpalm.tank.vo.ga.FitnessCalc;
import com.ourpalm.tank.vo.ga.Individual;

public class TeamFitnessCalc implements FitnessCalc {

	private List<Team> teams;
	private int size;

	private int preferredSideSize;
	private double totalValue;

	private List<DeadFitnessRule> deadRules;

	public TeamFitnessCalc(List<Team> teams) {
		this.teams = teams;
		this.size = teams.size();

		for (Team team : teams) {
			this.preferredSideSize += team.sizeOfTanks();
			this.totalValue += team.getBattleScore();
		}

		this.preferredSideSize = this.preferredSideSize >>> 1;
		this.deadRules = new ArrayList<>();

		initDeadRules();
	}

	/**
	 * 初始化致死基因判断规则
	 */
	private void initDeadRules() {
		deadRules.add(new TeamSizeRule());
		deadRules.add(new NumberOfTankDestoryerRule());
	}

	public List<Team> filterTeams(int gene) {
		List<Team> red = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			if ((gene & (1 << i)) != 0) {
				red.add(teams.get(i));
			}
		}
		return red;
	}

	@Override
	public double getFitness(Individual individual) {
		// VR 表示红队队伍总价值，VB 表示蓝队队伍总价值

		// 1. 基因映射到价值
		int redGene  = individual.getGene();
		int l = individual.lengthOfGene();

		// 2. 致死基因判断
		for (DeadFitnessRule dead : deadRules) {
			if (dead.eval(teams, redGene, l)) {
				return 0;
			}
		}

		// 3. 计算适应度：包括计算价值和方差
		double VR = getValueOfTeam(redGene);
		// double VARR = 1; // getVarianceOfTeam(redGene, VR * 1.0 / preferredSideSize);

		double VB = totalValue - VR;
		// double VARB = 1; // getVarianceOfTeam(blueGene, VB * 1.0 / preferredSideSize);

		// 总价值 VR * VB 越大越好
		// 队伍方差越小越好
		double fitness = VR * VB;
		return fitness;
	}

	private double getValueOfTeam(int gene) {
		double value = 0;
		for (int i = 0; i < size; i++) {
			if ((gene & (1 << i)) != 0) {
				Team team = teams.get(i);
				value += team.getBattleScore();
			}
		}
		return value;
	}

//	private double getVarianceOfTeam(int gene, double average) {
//		double sum = 0;
//		for (int i = 0; i < size; i++) {
//			if ((gene & (1 << i)) != 0) {
//				Team team = teams.get(i);
//				for (TeamItem rm : team.getTanks()) {
//					sum += rm.getLevel();
//				}
//			}
//		}
//		return sum;
//	}

	private int getNumberOfTankDestroyer(int gene) {
		int num = 0;
		for (int i = 0; i < size; i++) {
			if ((gene & (1 << i)) != 0) {
				Team team = teams.get(i);
				for (TeamItem rm : team.getTanks()) {
					if (rm.getType() == 4) {
						num += 1;
					}
				}
			}
		}
		return num;
	}

	/**
	 * 致死基因判断条件：队伍人数不平均
	 */
	private class TeamSizeRule implements DeadFitnessRule {

		@Override
		public boolean eval(List<Team> teams, int gene, int lengthOfGene) {
			int sizeOfSide = 0;
			for (int i = 0; i < lengthOfGene; i++) {
				if ((gene & (1 << i)) != 0) {
					Team team = teams.get(i);
					sizeOfSide += team.sizeOfTanks();
				}
			}
			// 队伍人数不平均，适应度函数为 0，废柴基因
			return sizeOfSide != TeamFitnessCalc.this.preferredSideSize;
		}
	}

	/**
	 * 致死基因判断条件：TD 数量限制
	 */
	private class NumberOfTankDestoryerRule implements DeadFitnessRule {

		@Override
		public boolean eval(List<Team> teams, int gene, int lengthOfGene) {
			// 1. 数量大于 3，废柴基因
			int r_numOfTD = getNumberOfTankDestroyer(gene);
			if (r_numOfTD > 3) {
				return true;
			}
			int b_numOfTD = getNumberOfTankDestroyer(Individual.mask(~gene, lengthOfGene));
			if (b_numOfTD > 3) {
				return true;
			}
			// 2. 两边差值大于 1，废柴基因
			return (Math.abs(r_numOfTD - b_numOfTD)) > 1;
		}
	}
}
