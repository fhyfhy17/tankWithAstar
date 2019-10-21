package com.ourpalm.tank.app.match.ga;

import java.util.List;

import com.ourpalm.tank.app.match.Team;

public interface DeadFitnessRule {

	/**
	 * 致死基因判断条件
	 *
	 * @param teams
	 * @param gene
	 * @param lengthOfGene
	 * @return
	 */
	boolean eval(List<Team> teams, int gene, int lengthOfGene);
}
