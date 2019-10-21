package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleFriend {
	private Set<Integer> friends;			//好友roleId
	private Set<Integer> friendApplys;		//好友申请roleId
	private Map<Integer, Long> sendGoodsTime;	//赠送给好友礼物的时间
	private Set<Integer> receiveGoods;		//哪些好友赠送过礼物
	
	private long lastRecommondTime = 0;		//上次请求推荐时间
	
	private long lastDeleteTime = 0; 	//上次删除好友时间
	private int deleteCount = 0;		//今日删除好友个数
	
	public RoleFriend() {
		this.friends = new HashSet<>();
		this.friendApplys = new HashSet<>();
		this.sendGoodsTime = new HashMap<>();
		this.receiveGoods = new HashSet<>();
	}

	public Set<Integer> getFriends() {
		return friends;
	}
	
	public Set<Integer> getApplicant() {
		return friendApplys;
	}

	public Map<Integer, Long> getSendGoodsTime() {
		return sendGoodsTime;
	}

	public Set<Integer> getReceiveGoods() {
		return receiveGoods;
	}
	
	public void addFriend(int friendId) {
		friends.add(friendId);
	}
	
	public void addFriendApplicant(int friendId) {
		friendApplys.add(friendId);
	}
	
	public void addGoodsTime(int friendId) {
		long t = System.currentTimeMillis();
		sendGoodsTime.put(friendId, t);
	}
	
	public void receiveGoods(int friendId) {
		receiveGoods.add(friendId);
	}
	
	public void removeFriend(int friendId) {
		friends.remove(friendId);
		sendGoodsTime.remove(friendId);
	}

	public void removeFriendApplicant(int friendId) {
		friendApplys.remove(friendId);
	}
	
	public void removeFriendApplys(List<Integer> fids) {
		friendApplys.removeAll(fids);
	}
	
	public void removeReceiveGoods(int friendId) {
		receiveGoods.remove(friendId);
	}
	
	public void removeReceiveGoods(List<Integer> friendIds) {
		receiveGoods.removeAll(friendIds);
	}

	public long getLastRecommondTime() {
		return lastRecommondTime;
	}

	public void setLastRecommondTime(long lastRecommondTime) {
		this.lastRecommondTime = lastRecommondTime;
	}

	public long getLastDeleteTime() {
		return lastDeleteTime;
	}

	public void setLastDeleteTime(long lastDeleteTime) {
		this.lastDeleteTime = lastDeleteTime;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}
	
	public void incrementDeleteCount() {
		this.deleteCount++;
	}
	
	
	
}
