package com.ourpalm.tank.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ourpalm.tank.util.peshe.Peshe;

public class RandomUtil {

	private static SecureRandom random = new SecureRandom();

	@SuppressWarnings("unchecked")
	public static <T extends Peshe> T getPeshe(List<T> pesheList) {
		if (null == pesheList || 0 == pesheList.size()) {
			return null;
		}
		int sumGon = 0;
		for (Peshe item : pesheList) {
			sumGon += item.getGon();
		}
		if (0 == sumGon) {
			return null;
		}
		int random = randomIntWithoutZero(sumGon);
		int overlapCount = 0;
		for (Peshe it : pesheList) {
			if ((overlapCount < random) && (random <= (overlapCount + it.getGon()))) {
				return (T) it;
			}
			overlapCount += it.getGon();
		}
		return null;
	}

	public static int randomIntWithoutZero(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("随机数上限错误:" + limit);
		}
		int result = Math.abs(random.nextInt()) % limit;
		if (0 == result) {
			return 1;
		}
		return result + 1;
	}

	public static int randomInt(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("随机数上限错误:" + limit);
		}
		return Math.abs(random.nextInt()) % limit;
	}

	public static int randomInt(int min, int max) {
		int diff = max - min;
		if (diff <= 0) {
			throw new IllegalArgumentException("随机数最大值必须大于最小值");
		}
		return randomInt(diff) + min;
	}

	public static int randomInt() {
		return Math.abs(random.nextInt());
	}

	/**
	 * 随机一组不重复的随机数
	 * 
	 * @param limit
	 *            随机数的最大范围值
	 * @param count
	 *            需要的随机数个数
	 * @return
	 */
	public static List<Integer> randomIntList(int limit, int count) {
		if (limit <= 0 || count <= 0) {
			throw new IllegalArgumentException("参数错误,必须大于0 limit = " + limit + " count = " + count);
		}
		List<Integer> randomList = new ArrayList<>(count);
		while (true) {
			if (randomList.size() >= count) {
				break;
			}
			int random = randomInt(limit);
			if (randomList.contains(random)) {
				continue;
			}
			randomList.add(random);
		}
		return randomList;
	}

	/**
	 * 在set 里随机一个
	 * 
	 * @param set
	 * @return
	 */
	public static <E> E getRandomElement(Set<E> set) {
		int rn = randomInt(set.size());
		int i = 0;
		for (E e : set) {
			if (i == rn) {
				return e;
			}
			i++;
		}
		return null;
	}
}
