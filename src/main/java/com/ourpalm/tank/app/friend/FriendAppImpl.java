package com.ourpalm.tank.app.friend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.FriendDao;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleFriend;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendGoods;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_TIP_MSG;
import com.ourpalm.tank.template.FriendTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;


public class FriendAppImpl implements FriendApp{
	
	private Logger logger = LogCore.runtime;
	
	private FriendDao friendDao;
	
	private FriendTemplate friendTemplate;
	
	public void loadTemplate() {
		String sourceFile = XlsSheetType.FriendTemplate.getXlsFileName();
		String sheetName = XlsSheetType.FriendTemplate.getSheetName();
		try {
			friendTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, FriendTemplate.class).get(0);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	@Override
	public void login(int roleId) {
		RoleFriend info = friendDao.get(roleId);
		if(info == null) {
			info = new RoleFriend();
			friendDao.save(roleId, info);
		}
	}
	
	private RoleFriend getRoleFriend(int roleId) {
		RoleFriend info = friendDao.get(roleId);
		return info;
	}
	
	@Override
	public Set<Integer> getAllFriend(int roleId) {
		RoleFriend info = getRoleFriend(roleId);
		return info.getFriends();
	}

	@Override
	public Set<Integer> getAllApplicant(int roleId) {
		RoleFriend info = getRoleFriend(roleId);
		return info.getApplicant();
	}
	
	@Override
	public RoleAccount findFriendByName(String friendName) {
		int friendId = GameContext.getUserApp().getRoleId(friendName);
		return GameContext.getUserApp().getRoleAccount(friendId);
	}
	
	@Override
	public List<RoleAccount> recommendFriend(int roleId, int areaId) {
//		RoleFriend info = getRoleFriend(roleId);
//		List<RoleAccount> roleList = new ArrayList<>();
//		
//		long lastTime = info.getLastRecommondTime(), curTime = System.currentTimeMillis();
//		if(curTime - lastTime >= 1000) {
//			int  recommendCount = friendTemplate.getRecommendCount();
//			List<Integer> friendIdList = GameContext.getUserApp().getAllRoleId(areaId);
//			int roleSize = friendIdList.size(), rIndex = RandomUtil.randomInt(roleSize);
//			
//			if(logger.isDebugEnabled()){
//				logger.debug("{}, 请求推荐好友 列表={}, rIndex: {}", roleId, Arrays.toString(friendIdList.toArray()), rIndex);
//			}
//			
//			for(int i = rIndex, 
//					l = rIndex + recommendCount > roleSize ? roleSize : rIndex + recommendCount;  
//					i < l; i++) {
//				int fId = friendIdList.get(i);
//				
//				if(isFriend(roleId, fId)) {
//					logger.debug("{}, 请求推荐好友已经是好友不推荐={}", roleId, fId);
//					continue;
//				}
//				
//				if(fId != roleId && hadOnline(fId)) {	//不是自己，且在线
//					RoleAccount role = GameContext.getUserApp().getRoleAccount(fId);
//					roleList.add(role);
//				}
//			}
//			
//			info.setLastRecommondTime(curTime);
//			friendDao.save(roleId, info);
//		} else {
//			return null;
//		}
//		return roleList;
		return new ArrayList<>();
	}

	@Override
	public void addFirendApplicant(int roleId, int friendId) {
		if(friendId <= 0 || !hadInit(friendId)) {
			return;
		}
		
		RoleFriend info = getRoleFriend(friendId);
		if(info == null)
			return;
		
		info.addFriendApplicant(roleId);
		friendDao.save(friendId, info);
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null) {
			return;
		}
		String msg = account.getRoleName() + " 请求添加你为好友！";
		prompt(friendId, msg);
	}
	
	private void prompt(int roleId, String msg) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_FRIEND_TIP_MSG.Builder builder = STC_FRIEND_TIP_MSG.newBuilder();
			builder.setInfo(msg);
			connect.sendMsg(FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, FRIEND_MSG.CMD_ID.STC_FRIEND_TIP_VALUE, builder.build().toByteArray());
		}
	}

	@Override
	public Result deleteFriend(int roleId, int friendId) {
		if(!hadInit(friendId))
			return Result.newFailure("没有该好友");
		
		RoleFriend info = getRoleFriend(roleId);
		long lastTime = info.getLastDeleteTime();
		long curTime = System.currentTimeMillis();
		
		if(!DateUtil.isSameDay(lastTime, curTime)) {
			logger.debug("{}, 删除好友一天开始", roleId);
			info.setDeleteCount(0);
		} 
		
		/*//暂不开启
		if(info.getDeleteCount() >= friendTemplate.getDeleteCount()) {
			logger.debug("{}, 删除好友一天达到上限", roleId);
			return Result.newFailure(String.format(Tips.FRIEND_DELETE, friendTemplate.getDeleteCount()));
		}*/
		
		info.setLastDeleteTime(curTime);
		info.incrementDeleteCount();
		info.removeFriend(friendId);
		
		RoleFriend fInfo = getRoleFriend(friendId);		//对方删除
		fInfo.removeFriend(roleId);
		
		friendDao.save(roleId, info);
		friendDao.save(friendId, fInfo);
		return Result.newSuccess();
	}

	@Override
	public Result confirmFriend(int roleId, int confirmId) {
		if(roleId == confirmId) {
			return Result.newFailure("添加好友失败");
		}
		
		RoleFriend info = getRoleFriend(roleId);
		
		if(info.getFriends().size() == friendTemplate.getFriendCount()) {
			logger.debug("{}, 确认好友达到上限", roleId);
			
			info.removeFriendApplicant(confirmId);
			friendDao.save(roleId, info);
			return Result.newFailure(Tips.FRIEND_MAX_COUNT);
		}
		
		if(!friendDao.hadInit(confirmId)) {
			return Result.newFailure("好友数据初始化失败");
		}
		
		RoleFriend _fInfo = getRoleFriend(confirmId);
		if(_fInfo.getFriends().size() == friendTemplate.getFriendCount()) {
			logger.debug("{}, 确认好友 {}, 达到好友上限", roleId, confirmId);
			
			info.removeFriendApplicant(confirmId);
			friendDao.save(roleId, info);
			return Result.newFailure(Tips.FRIEND_OTHER_MAX_COUNT);
		}
		
		if(isFriend(roleId, confirmId)) {//已经是好友了
			logger.debug("{}, 已经是好友 {} ", roleId, confirmId);
			
			info.removeFriendApplicant(confirmId);
			friendDao.save(roleId, info);
			return Result.newFailure(Tips.FRIEND_IS_FRIEND);
		}
		
		info.addFriend(confirmId);
		info.removeFriendApplicant(confirmId);
		friendDao.save(roleId, info);
		
		_fInfo.addFriend(roleId);
		friendDao.save(confirmId, _fInfo);
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account != null) {
			String msg = account.getRoleName() + " 添加你为好友！";
			prompt(confirmId, msg);
		}
		return Result.newSuccess();
	}
	
	@Override
	public void refuseFriend(int roleId, int refusedId) {
		RoleFriend info = getRoleFriend(roleId);
		info.removeFriendApplicant(refusedId);
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		
		STC_FRIEND_TIP_MSG.Builder tip = STC_FRIEND_TIP_MSG.newBuilder();
		tip.setInfo(String.format(Tips.FRIEND_REFUSE_TIP, role.getRoleName()));
		RoleConnect friendConnect = GameContext.getUserApp().getRoleConnect(refusedId);
		if(friendConnect != null) {
			friendConnect.sendMsg(FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, FRIEND_MSG.CMD_ID.STC_FRIEND_TIP_VALUE, tip.build().toByteArray());
		}
		
		friendDao.save(roleId, info);
	}

	@Override
	public List<Integer> sendGoods(int roleId, List<Integer> friendIds) {
		List<Integer> sendIds = new ArrayList<Integer>();
		RoleFriend info = getRoleFriend(roleId);
		Map<Integer, Long> timeMap = info.getSendGoodsTime();
		
		for(int fId : friendIds) {
			if(!friendDao.hadInit(fId))
				continue;
			
			if(timeMap.containsKey(fId)) {
				Long t = timeMap.get(fId);
				if(DateUtil.isSameDay(t, System.currentTimeMillis())) {		//一天一次
					logger.debug("{}, 请求赠送给 {}, 是同一天不能再送了", roleId, fId);
					continue;
				}
			}
			
			info.addGoodsTime(fId);
			sendIds.add(fId);
			
			RoleFriend _fInfo = getRoleFriend(fId);
			_fInfo.receiveGoods(roleId);
			friendDao.save(fId, _fInfo);
		}
		
		friendDao.save(roleId, info);
		return sendIds;
	}

	@Override
	public List<Integer> receiveGoods(int roleId, List<Integer> friendIds) {
		RoleFriend info = getRoleFriend(roleId);
		
		List<Integer> list = new ArrayList<Integer>();
		for(int fId : friendIds) {
			if(info.getReceiveGoods().contains(fId)) {
				info.removeReceiveGoods(fId);
				list.add(fId);
			} else {
				logger.debug("{}, 没有接收好友礼物, {}", roleId, fId);
			}
		}
	
		friendDao.save(roleId, info);
		return list;
	}
	
	
	public List<FriendGoods> receiveGoodsList(int roleId) {
		RoleFriend info = getRoleFriend(roleId);
		List<FriendGoods> list = new ArrayList<FriendGoods>();
		Set<Integer> fIds = info.getReceiveGoods();
		for(int fid : fIds) {
			FriendGoods.Builder goods = FriendGoods.newBuilder();
			goods.setRoleId(fid);
			goods.setGold(friendTemplate.getGold());
			list.add(goods.build());
		}
		return list;
	}

	@Override
	public void start() {
		loadTemplate();
	}

	@Override
	public void stop() {
	}
	
	@Override
	public int getIronCount() {
		return friendTemplate.getIron();
	}
	
	@Override
	public int getGoldCount() {
		return friendTemplate.getGold();
	}
	
	public boolean canAddFriend(int size) {
		return size < friendTemplate.getFriendCount();
	}
	
	/**
	 * roleId 玩家id
	 * friend 该玩家的好友信息
	 * return 好友信息
	 */
	public FriendInfo buildFriendInfo(int roleId, RoleAccount friend) {
		int friendId = friend.getRoleId();
		FriendInfo.Builder info = FriendInfo.newBuilder();
		info.setRoleId(friendId);
		info.setLevel(friend.getLevel());
		info.setTitleId(friend.getCurrTitleId());
		info.setName(friend.getRoleName());
		info.setVip(friend.getVipLevel());	
		info.setIsOnline(hadOnline(friendId));
		info.setTankTempId(friend.getMainTankId());
		info.setCorpsName("");
		info.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(friendId));
		info.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(friendId));
		
		int corpsId = GameContext.getUserApp().getCorpsId(friend.getRoleId());
		if(corpsId > 0){
			CorpsInfo corpsInfo = GameContext.getCorpsApp().getCorpsInfo(corpsId);
			if(corpsInfo != null) {
				info.setCorpsName(corpsInfo.getName());
			}
		}
		
		RoleTank tank = GameContext.getTankApp().getRoleTank(friendId, friend.getMainTankId());
		info.setIsElite(false);
		if(tank != null)
			info.setIsElite(false);
		
		if(!info.getIsOnline()) {
			Date date = friend.getLastLoginDate();
			long last = DateUtil.convertDateToMillis(date);
			long now = System.currentTimeMillis();
			info.setOutTime((int)(now - last) / 1000);
		}
		
		//玩家当天赠送过好友
		RoleFriend roleInfo = getRoleFriend(roleId);
		Map<Integer, Long> sendMap = roleInfo.getSendGoodsTime();
		if(sendMap.containsKey(friendId)) {
			long t = sendMap.get(friendId);
			if(DateUtil.isSameDay(t, System.currentTimeMillis()))
				info.setBeSendGoods(true);
		}
		//好友当天赠送过玩家
		RoleFriend fInfo = getRoleFriend(friendId);
		if(fInfo != null) {
			sendMap = fInfo.getSendGoodsTime();
			if(sendMap.containsKey(roleId)) {
				long t = sendMap.get(roleId);
				if(DateUtil.isSameDay(t, System.currentTimeMillis())) 
					info.setHadSendGoods(true);
			}
		}
		
		return info.build();
	}

	private boolean hadOnline(int roleId){
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		return connect != null;
	}
	
	public boolean isFriend(int roleId, int fid) {
		RoleFriend rInfo = getRoleFriend(roleId);
		if(rInfo.getFriends().contains(fid))
			return true;
		return false;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	@Override
	public boolean hadInit(int friendId) {
		return friendDao.hadInit(friendId);
	}

	@Override
	public RoleFriend get(int roleId) {
		return friendDao.get(roleId);
	}
	
	@Override
	public int getFriendMax() {
		return friendTemplate.getFriendCount();
	}

	@Override
	public boolean hadApplicant(int roleId, int friendId) {
		if(friendId <= 0 || !hadInit(friendId)) {
			return false;
		}
		
		RoleFriend info = getRoleFriend(friendId);
		return info.getApplicant().contains(roleId);
	}

	@Override
	public RoleAccount findFriendById(int roleId) {
		return GameContext.getUserApp().getRoleAccount(roleId);
	}
}
