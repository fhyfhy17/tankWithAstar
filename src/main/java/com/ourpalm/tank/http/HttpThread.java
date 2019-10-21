package com.ourpalm.tank.http;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.thread.ThreadId;

public class HttpThread extends Thread {
	private Logger logger = LogCore.runtime;
	public volatile boolean flag = false;
	private final int SLEEP_TIME = 50;
	private ConcurrentLinkedQueue<HttpEvent> httpEventQueue = new ConcurrentLinkedQueue<>();

	public int getThreadId() {
		return ThreadId.HTTP;
	}

	@Override
	public void run() {
		super.run();
		while (flag) {
			if (httpEventQueue.isEmpty())
				return;
			while (!httpEventQueue.isEmpty()) {
				HttpEvent event = httpEventQueue.poll();
				if (event == null)
					continue;
				excuteEvent(event);
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 执行发送 */
	protected void excuteEvent(HttpEvent event) {
		String result = null;
		if (event.isGet()) {
			String url = event.getUrl();
			if (StringUtil.isNotEmpty(event.getParams())) {
				if (url.contains("?")) {
					url += "&" + event.getParams();
				} else {
					url += "?" + event.getParams();
				}
			}
			result = HttpSend.sendGet(url);
		} else {
			result = HttpSend.sendPost(event.getUrl(), event.getParams());
		}
		callSendDataBack(event, result);
	}

	/** 处理结果 */
	protected void callSendDataBack(HttpEvent event, String result) {
		event.call();
	}

	public void add(HttpEvent httpEvent) {
		if (httpEvent == null)
			logger.error("http 添加到队列的对象为空");
		httpEventQueue.offer(httpEvent);
	}

}
