package com.ourpalm.tank.vo.ga;

public interface FitnessCalc {

	/**
	 * 对给定的 {@code Individual} 个体计算适应度。
	 *
	 * @param individual 个体
	 * @return           个体的适应度
	 */
	double getFitness(Individual individual);
}
