package com.ourpalm.tank.vo.ga;

import java.util.concurrent.ThreadLocalRandom;

public class Individual {

	/** 基因型 */
	private int gene;
	/** 基因型比特位长度 */
	private int lengthOfGene;

	/**
	 * 以特定的基因值初始化一个个体，并指定基因的比特长度，基因值超过长度的比特部分将被忽略。
	 *
	 * @param gene         基因值
	 * @param lengthOfGene 基因值的有效比特长度
	 */
	public Individual(int gene, int lengthOfGene) {
		// 初始化基因型
		setGenes(gene, lengthOfGene);
	}

	public static Individual generate(int lengthOfGene) {
		// 随机生成基因型
		int gene = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
		return new Individual(gene, lengthOfGene);
	}

	public int lengthOfGene() {
		return lengthOfGene;
	}

	/**
	 * 返回当前个体的基因型。
	 *
	 * @return 个体的基因型
	 */
	public int getGene() {
		return gene;
	}

	/**
	 * 为个体设置新的基因型。
	 *
	 * @param gene         新基因型
	 * @param lengthOfGene 有效基因长度
	 */
	public void setGenes(int gene, int lengthOfGene) {
		this.lengthOfGene = lengthOfGene;
		this.gene = mask(gene, lengthOfGene);
	}

	/**
	 * 将基因值做有效长度的掩码运算，只保留有效长度内的比特位。
	 *
	 * @param gene         基因值
	 * @param lengthOfGene 基因值的有效比特长度
	 * @return             运算后的基因值
	 */
	public static int mask(int gene, int lengthOfGene) {
		return gene & ((1 << lengthOfGene) - 1);
	}

	/**
	 * 将本个体的基因值与目标个体的基因值做交叉运算，交叉的位置由 {@code pos} 指定。
	 *
	 * @param individual 目标个体
	 * @param pos        基因交叉位置
	 */
	public void crossOver(Individual individual, int pos) {
		// 个体间的基因长度一致才能交叉
		if (lengthOfGene != individual.lengthOfGene) {
			throw new IllegalArgumentException();
		}
		if (pos == 0) {
			return;
		}
		int mask = (1 << pos) - 1;
		int MA = gene & mask;
		int MB = individual.gene & mask;

		gene >>>= pos;
		gene <<= pos;
		gene |= MB;
		gene = mask(gene, lengthOfGene);

		individual.gene >>>= pos;
		individual.gene <<= pos;
		individual.gene |= MA;
		individual.gene = mask(individual.gene, lengthOfGene);
	}

	/**
	 * 在 {@code pos} 位置上对本个体基因值做取反操作。
	 *
	 * @param pos 基因突变位置
	 */
	public void mutate(int pos) {
		this.gene ^= (1 << pos);
	}

	/**
	 * 返回个体的适应度，需要指定适应度计算函数 {@code FitnessCalc}。
	 *
	 * @param calc 适应度计算函数
	 * @return     个体的适应度
	 */
	public double getFitness(FitnessCalc calc) {
		return calc.getFitness(this);
	}

	@Override
	public String toString() {
		return String.valueOf(gene);
	}
}
