package com.ourpalm.tank.app.friend;

import java.util.List;
import java.util.Set;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleFriend;
import com.ourpalm.tank.message.FRIEND_MSG.FriendGoods;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.vo.result.Result;

public interface FriendApp  extends Service{
	/**
	 * 获取好友列表 
	 * @param roleId
	 * @return	好友roleId列表
	 */
	Set<Integer> getAllFriend(int roleId);
	/**
	 * 获取好友申请列表
	 * @param roleId
	 * @return 申请者roleId列表
	 */
	Set<Integer> getAllApplicant(int roleId);
	//查找好友
	RoleAccount findFriendByName(String friendName);
	RoleAccount findFriendById(int roleId);
	//推荐好友
	List<RoleAccount> recommendFriend(int roleId, int areaId);
	//发送好友申请
	void addFirendApplicant(int roleId, int friendId);		
	//删除好友
	Result deleteFriend(int roleId, int friendId);
	//添加好友
	Result confirmFriend(int roleId, int confirmedId);
	//拒绝添加好友
	void refuseFriend(int roleId, int refusedId);
	
	//获取好友上限值
	int getFriendMax();
	/**
	 * 赠送礼物
	 * @param roleId	玩家id
	 * @param friendIds	好友id列表
	 * @return	赠送成功的好友id列表
	 */
	List<Integer> sendGoods(int roleId, List<Integer> friendIds);
	/**
	 * 接收礼物
	 * @param roleId	玩家id
	 * @param friendIds	好友id列表
	 * @return	获取金币数量
	 */
	List<Integer> receiveGoods(int roleId, List<Integer> friendIds);
	
	//请求收礼界面
	List<FriendGoods> receiveGoodsList(int roleId);
	
	void login(int roleId);
	
	int getIronCount();
	
	int getGoldCount();
	
	boolean canAddFriend(int size);
	
	boolean isFriend(int roleId, int fid);
	
	FriendInfo buildFriendInfo(int roleId, RoleAccount friend);
	
	boolean hadInit(int friendId);
	
	RoleFriend get(int roleId);
	
	boolean hadApplicant(int roleId, int friendId);
}
