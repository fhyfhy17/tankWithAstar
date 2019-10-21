package com.ourpalm.tank.vo.ga;

import java.util.ArrayList;
import java.util.List;

public class Population {

	/** 种群中包含的个体 */
	private Individual[] individuals;
	/** 适应度计算函数 */
	private FitnessCalc fitnessCalc;

	private Population(int sizeOfPopulation, FitnessCalc fitnessCalc) {
		// 指定种群大小
		individuals = new Individual[sizeOfPopulation];
		// 指定适应度计算函数
		this.fitnessCalc = fitnessCalc;
	}

	public static Population createEmpty(int sizeOfPopulation, FitnessCalc fitnessCalc) {
		return new Population(sizeOfPopulation, fitnessCalc);
	}

	public void initialize(int lengthOfGene) {
		for (int i = 0; i < individuals.length; i++) {
			// 初始化种群
			individuals[i] = Individual.generate(lengthOfGene);
		}
	}

	public void setIndividual(int index, Individual individual) {
		individuals[index] = individual;
	}

	public Individual getIndividual(int index) {
		return individuals[index];
	}

	/**
	 * 返回适应度最高的个体
	 *
	 * @return 适应度最高的个体
	 */
	public Individual getFittest() {
        Individual fittest = individuals[0];
        FitnessCalc fc = fitnessCalc;
		for (Individual i : individuals) {
			if (fittest.getFitness(fc) <= i.getFitness(fc)) {
				fittest = i;
			}
		}
		return fittest;
	}

	public int size() {
		return individuals.length;
	}

	public FitnessCalc getFitnessCalc() {
		return fitnessCalc;
	}

	Population shallowCopy() {
		Population copy = createEmpty(individuals.length, fitnessCalc);
		for (int i = 0; i < individuals.length; i++) {
			copy.setIndividual(i, individuals[i]);
		}
		return copy;
	}

	@Override
	public String toString() {
		List<Individual> inds = new ArrayList<>();
		for (Individual ind : individuals) {
			inds.add(ind);
		}
		return inds.toString();
	}
}
