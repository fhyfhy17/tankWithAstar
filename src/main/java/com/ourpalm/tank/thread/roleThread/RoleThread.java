package com.ourpalm.tank.thread.roleThread;

import java.util.concurrent.LinkedBlockingDeque;

public class RoleThread extends Thread {
	private final int SLEEP_TIME = 5;
	private LinkedBlockingDeque <RoleEvent> eventQueue = new LinkedBlockingDeque <>();

	@Override
	public void run() {
		for (;;) {
			if(eventQueue.isEmpty()){
				continue;
			}
			try {
				RoleEvent e = eventQueue.poll();
				if (e != null) {
					boolean b = e.excute();
					if (!b) {
						eventQueue.offerLast(e);
					}
				}
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void add(RoleEvent e) {
		if (e != null) {
			eventQueue.offer(e);
		}
	}

}
