package com.ourpalm.tank.app.im;

import static com.ourpalm.tank.message.ROLE_MSG.ChatMessageType.System;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.online.OnlineCenter;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.InstantMessageDao;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.InstantMessageInfo;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.ROLE_MSG.ChatMessageType;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_IMESSAGE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_SYSTEM_IMESSAGE_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_IMESSAGE_SEND_MSG;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.MatchTank;
import com.ourpalm.tank.vo.result.Result;

public class InstantMessageAppImpl implements InstantMessageApp {
	private static Logger logger = LogCore.runtime;
	private InstantMessageDao imDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendSystem(String msg) {
		sendSystem(msg, System);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendSystem(String msg, ChatMessageType channel) {
		sendSystem(msg, channel, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendSystem(String msg, ChatMessageType channel, Collection<Integer> roles) {

		if (logger.isDebugEnabled()) {
			logger.debug("发送系统聊天");
		}

		STC_SYSTEM_IMESSAGE_MSG.Builder builder = STC_SYSTEM_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(channel);

		Collection<RoleConnect> allConnect = null;
		OnlineCenter app = GameContext.getOnlineCenter();

		if (Util.isEmpty(roles)) {
			allConnect = app.getAllRoleConnect();
		} else {
			allConnect = new ArrayList<>(roles.size());
			RoleConnect c;
			for (Integer roleId : roles) {
				c = app.getRoleConnectByRoleId(roleId);
				if (c != null) {
					allConnect.add(c);
				}
			}
		}

		for (RoleConnect connect : allConnect) {
			connect.sendMsg(builder.build());
		}

		Message m = buildToRemoteMessage(channel.getNumber(), msg);
		RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
		if (remoteNode != null) {
			remoteNode.sendReqMsg(m);
		}

//		Collection<RemoteNode> allRemoteNode = GameContext.getGameManagerApp().getAllRemoteNode();
//		for(RemoteNode node : allRemoteNode) {
//			node.sendReqMsg(sysMsg);
//		}
	}

	@Override
	public Result sendAllServer(int roleId, String msg) {
		int level = GameContext.getUserApp().getRoleAccount(roleId).getLevel();
		if(level < 10){
			return Result.newFailure("聊天功能需要账号等级10级开放");
		}
		
		final int useGoodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.broadcast);
		boolean hadUse = GameContext.getGoodsApp().removeGoods(roleId, useGoodsId, 1, "全服聊天");
		if(!hadUse) {
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(useGoodsId);
			if(template == null) {
				return Result.newFailure("发送失败");
			}
			hadUse = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, template.getGold()), OutputType.allServerTellDec);
			if(!hadUse) {
				return Result.newFailure("金币不足，发送失败");
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("发送全服聊天");
		}
		
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.AllServer);
		
		Collection<RoleConnect> allConnect = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : allConnect) {
			if(connect.getRoleId() == roleId)
				continue;
			
			connect.sendMsg(builder.build());
		}
		
		Message sysMsg = buildToRemoteMessage(ChatMessageType.AllServer_VALUE, msg);
		RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
		if(remoteNode != null){
			remoteNode.sendReqMsg(sysMsg);
		}
//		Collection<RemoteNode> allRemoteNode = GameContext.getGameManagerApp().getAllRemoteNode();
//		for(RemoteNode node : allRemoteNode) {
//			node.sendReqMsg(sysMsg);
//		}
		
		return Result.newSuccess();
	}
	
	@Override
	public Result sendOwnServer(int roleId, int areaId, String msg) {
		int level = GameContext.getUserApp().getRoleAccount(roleId).getLevel();
		if(level < 10){
			return Result.newFailure("聊天功能需要账号等级10级开放");
		}
		final int useGoodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.broadcast);
		boolean hadUse = GameContext.getGoodsApp().removeGoods(roleId, useGoodsId, 1, "本服聊天");
		if(!hadUse) {
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(useGoodsId);
			if(template == null) {
				return Result.newFailure("发送失败");
			}
			hadUse = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, template.getGold()), OutputType.localServerTellDec);
			if(!hadUse) {
				return Result.newFailure("金币不足，发送失败");
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("发送本服聊天");
		}
		
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.World);
		
		Collection<RoleConnect> allConnect = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : allConnect) {
			if(connect.getRoleId() == roleId || connect.getAreaId() != areaId)
				continue;
			
			connect.sendMsg(builder.build());
		}
		
		Message sysMsg = buildToRemoteMessage(ChatMessageType.World_VALUE, msg, areaId);
		RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
		if(remoteNode != null){
			remoteNode.sendReqMsg(sysMsg);
		}
//		Collection<RemoteNode> allRemoteNode = GameContext.getGameManagerApp().getAllRemoteNode();
//		for(RemoteNode node : allRemoteNode) {
//			node.sendReqMsg(sysMsg);
//		}
		
		return Result.newSuccess();
	}

	@Override
	public Result sendCorps(int roleId, String msg) {
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if(corpsId <= 0){
			return Result.newFailure("玩家没有加入军团");
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("发送军团聊天");
		}
		
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.Corps);
		
		List<CorpsRole> corpsList = GameContext.getCorpsApp().getCorpsRoleList(corpsId);
		for(CorpsRole corpsRole : corpsList) {
			int _roleId = corpsRole.getRoleId();
			if(roleId == _roleId)
				continue;
			
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(_roleId);
			if(_connect == null)
				continue;
			
			_connect.sendMsg(builder.build());
		}
		
		return Result.newSuccess();
	}
	
	@Override
	public Result sendTeam(int roleId, String msg) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null) {
			return Result.newFailure("玩家不存在");
		}
		String teamId = account.getTeamId();
		if(Util.isEmpty(teamId)) {
			return Result.newSuccess();
		}
		
		MatchTeam team = GameContext.getMatchApp().getMatchTeam(teamId);
		if(team == null) {
			return Result.newSuccess();
		}
		if(logger.isDebugEnabled()){
			logger.debug("发送组队聊天");
		}
		
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.Team);
		
		for(MatchTank tank : team.allTeamTank()) {
			int froleId = tank.getRoleId();
			if(roleId == froleId){
				continue;
			}
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(froleId);
			if(connect != null) {
				connect.sendMsg(builder.build());
			}
		}
		
		return Result.newSuccess();
	}

	@Override
	public Result sendWhisper(int roleId, int receiverId, String msg) {
		if(logger.isDebugEnabled()){
			logger.debug("发送私聊天");
		}
		
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.Whisper);
		
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(receiverId);
		if(connect == null) {
			boolean isFriend = GameContext.getFriendApp().isFriend(roleId, receiverId);
			if(!isFriend) {
				return Result.newFailure("不是好友，不能发送离线消息");
			}
			
			RoleAccount account = GameContext.getUserApp().getRoleAccount(receiverId);
			if(account != null) {
				InstantMessageInfo info = imDao.get(receiverId);
				if(info == null)
					info = new InstantMessageInfo();
				
				info.add(msg);
				imDao.save(receiverId, info);
			}
		} else {
			connect.sendMsg(builder.build());
		}
		
		return Result.newSuccess();
	}
	
	@Override
	public void login(int roleId) {
		
		InstantMessageInfo info = imDao.get(roleId);
		if(info == null) {
			info = new InstantMessageInfo();
		}
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		
		for(String msg : info.getMsgList()) {
			STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
			builder.setMsg(msg);
			builder.setType(ChatMessageType.Whisper);
			
			connect.sendMsg(builder.build());
		}
		
		info.clear();
		imDao.save(roleId, info);
	}
	

	@Override
	public Result checkMsg(String text) {
		if(Util.isEmpty(text))
			return Result.newFailure("空消息");
		
		/*if(text.length() > msgAllowLen)
			return Result.newFailure("消息过长");*/
		
		Set<String> senWord = GameContext.getMutilDfaApp().matchSensitiveWord(text);
		if(!senWord.isEmpty())
			return Result.newFailure("消息存在屏蔽字：" + senWord.toString());
		
		return Result.newSuccess();
	}
	
	private Message buildToRemoteMessage(int type, String msg) {
		return buildToRemoteMessage(type, msg, 0);
	}
	
	private Message buildToRemoteMessage(int type, String msg, int areaId) {
		return buildToRemoteMessage(type, msg, areaId, 0);
	}
	
	private Message buildToRemoteMessage(int type, String msg, int areaId, int receiverId) {
		STS_IMESSAGE_SEND_MSG.Builder builder = STS_IMESSAGE_SEND_MSG.newBuilder();
		builder.setType(type);
		builder.setMsg(msg);
		builder.setAreaId(areaId);
		builder.setReceiverId(receiverId);
		
		Message sysMsg = new Message();
		sysMsg.setCmdType((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
		sysMsg.setCmdId((byte)SERV_MSG.CMD_ID.STS_IMESSAGE_SEND_VALUE);
		sysMsg.setFromNode(GameContext.getLocalNodeName());
		sysMsg.setData(builder.build().toByteArray());
		
		return sysMsg;
	}

	public void setImDao(InstantMessageDao imDao) {
		this.imDao = imDao;
	}
}
