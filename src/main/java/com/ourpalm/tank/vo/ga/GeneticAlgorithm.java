package com.ourpalm.tank.vo.ga;

public class GeneticAlgorithm {

	/** 突变率；过小导致进化缓慢，过大导致算法退化至暴力穷举 */
	private static final double MUTATE_RATE  = 0.04;
	/** 精英主义；若开启，种群中适应度最高的个体将完全保留至下一代种群中 */
	private static final boolean ELITISM     = true;

	/**
	 * 将种群进化至下一代
	 *
	 * @param pop 种群
	 * @return    下一代种群
	 */
	public static Population evolution(Population pop) {

		int size = pop.size();

		// 1. 自然选择
		natrualSelection(pop);

		int elitismOffset = ELITISM ? 1 : 0;

		// 2. 基因交叉
		for (int i = elitismOffset + 1; i < size; i += 2) {
			Individual male = pop.getIndividual(i);
			Individual female = pop.getIndividual(i + 1);
			int pos = (int) (Math.random() * male.lengthOfGene());
			male.crossOver(female, pos);
		}

		// 3. 基因突变
		for (int i = elitismOffset; i < size; i++) {
			Individual ind = pop.getIndividual(i);
			for (int pos = 0; pos < ind.lengthOfGene(); pos++) {
				if (Math.random() < MUTATE_RATE) {
					ind.mutate(pos);
				}
			}
		}

		return pop;
	}

	/**
	 * 自然选择，将种群自身迭代进化至下一代。
	 *
	 * @param pop 种群
	 */
	private static void natrualSelection(Population pop) {

		int size = pop.size();
		double[] fitnesses = new double[size];
		double totalFitness = 0;

		// 将种群浅复制一份
		Population copy = pop.shallowCopy();
		int elitismOffset = ELITISM ? 1 : 0;

		// 计算总适应度
		for (int i = elitismOffset; i < size; i++) {
			double fitness = pop.getIndividual(i).getFitness(pop.getFitnessCalc());
			fitness = ((int) (fitness * 100)) / 100.0;
			totalFitness += fitness;
			fitnesses[i] = totalFitness;
		}

		// 轮盘算法
		for (int i = elitismOffset; i < size; i++) {
			// 被选中的几率
			double chance = Math.random();
			for (int j = elitismOffset; j < fitnesses.length; j++) {
				if (fitnesses[j] > chance * totalFitness) {
					Individual origin = copy.getIndividual(i);
					Individual another = new Individual(origin.getGene(), origin.lengthOfGene());
					pop.setIndividual(i, another);
					break;
				}
			}
		}

		// 精英主义保留最优个体
		if (ELITISM) {
			Individual fittest = pop.getFittest();
			pop.setIndividual(0, new Individual(fittest.getGene(), fittest.lengthOfGene()));
		}
	}
}
