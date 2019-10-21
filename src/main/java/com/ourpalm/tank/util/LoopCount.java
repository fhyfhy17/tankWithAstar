package com.ourpalm.tank.util;

import java.util.Random;


public class LoopCount{
	private final static Random ran = new Random();
	private long cycle = 1 * 1000;
	private int count;
	private int index = 0;

	public LoopCount(long cycle, int loopSpeed) {
		this.cycle = cycle;
		this.count = Math.max(1, (int)(cycle/loopSpeed)) ;
		// 作用是避免在同一次轮询时全都执行，下一次轮询时全不执行
		index = ran.nextInt(this.count);
	}

	public void reset() {
		this.index = 0;
	}

	public void loop() {
		synchronized (this) {
			index += 1;
		}
	}

	public boolean reachCycle() {
		boolean isDo = (index >= count);
		if (isDo) {
			index = 0;
		}
		return isDo;
	}

	public boolean isReachCycle() {
		loop();
		return reachCycle();
	}

	public long getCycle() {
		return cycle;
	}

	public int getCount() {
		return count;
	}

	public int getIndex() {
		return index;
	}
}
