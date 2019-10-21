package com.ourpalm.tank.app.match;

import java.util.List;

import com.ourpalm.tank.app.match.ga.TeamFitnessCalc;
import com.ourpalm.tank.vo.ga.GeneticAlgorithm;
import com.ourpalm.tank.vo.ga.Individual;
import com.ourpalm.tank.vo.ga.Population;

public class TeamAllocator {

    ;

    public static List<Team> allocateWithGeneticAlgorithm(List<Team> teams) {

    	// 种群数量，一定要是偶数
    	int SIZE_OF_POPULATION = 20;
    	// 进化代数
    	int NUM_OF_GENERATION = 100;
    	int i = 0;

    	// 适应度计算函数
    	TeamFitnessCalc calc = new TeamFitnessCalc(teams);

    	// 初始化随机种群
    	Population pop = Population.createEmpty(SIZE_OF_POPULATION, calc);
    	pop.initialize(teams.size());

    	while (i++ < NUM_OF_GENERATION) {
    		// 不断进化
    		pop = GeneticAlgorithm.evolution(pop);
    		Individual best = pop.getFittest();
    		System.out.println(String.format("Best gene: %s with fitness: %d", best.getGene(), (int) best.getFitness(calc)));
    	}

    	// 取得种群中适应度最高的个体
    	Individual fittest = pop.getFittest();
    	// 获得最优基因
    	int gene = fittest.getGene();

    	// 分出队伍
    	return calc.filterTeams(gene);
    }
}
