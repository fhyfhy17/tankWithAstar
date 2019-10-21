package com.ourpalm.tank.domain;

import java.util.LinkedList;
import java.util.List;

public class InstantMessageInfo {
	private static final int cacheCount = 100;	//点对点发送，离线缓存消息数量
	
	private List<String> msgList = new LinkedList<>();

	public List<String> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<String> msgList) {
		this.msgList = msgList;
	}
	
	public void add(String msg) {
		if(msgList.size() >= cacheCount)
			removeFirst();
		
		this.msgList.add(msg);
	}
	
	public void removeFirst() {
		this.msgList.remove(0);
	}
	
	public void clear() {
		msgList.clear();
	}
	
}
