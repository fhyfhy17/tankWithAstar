package com.ourpalm.tank.app.im;

import java.util.Collection;

import com.ourpalm.tank.message.ROLE_MSG.ChatMessageType;
import com.ourpalm.tank.vo.result.Result;

public interface InstantMessageApp {

	/**
	 * 系统向全服发送消息
	 *
	 * @param msg 消息文本
	 */
	void sendSystem(String msg);

	/**
	 * 系统向特定频道发送消息
	 *
	 * @param msg     消息文本
	 * @param channel 目标频道
	 */
	void sendSystem(String msg, ChatMessageType channel);

	/**
	 * 系统向特定频道特定玩家发送消息
	 *
	 * @param msg     消息文本
	 * @param channel 目标频道
	 * @param roles   玩家 ID 集合
	 */
	void sendSystem(String msg, ChatMessageType channel, Collection<Integer> roles);

	/**
	 * 玩家向全服发送消息
	 *
	 * @param  roleId 玩家 ID
	 * @param  msg    消息文本
	 * @return        发送结果
	 */
	Result sendAllServer(int roleId, String msg);

	/**
	 * 玩家向本服发送消息
	 *
	 * @param  roleId 玩家 ID
	 * @param  areaId 分区 ID
	 * @param  msg    消息文本
	 * @return        发送结果
	 */
	Result sendOwnServer(int roleId, int areaId, String msg);

	/**
	 * 玩家向军团频道发消息
	 *
	 * @param  roleId 玩家 ID
	 * @param  msg    消息文本
	 * @return        发送结果
	 */
	Result sendCorps(int roleId, String msg);

	/**
	 * 玩家向组队频道发消息
	 *
	 * @param  roleId 玩家 ID
	 * @param  msg    消息文本
	 * @return        发送结果
	 */
	Result sendTeam(int roleId, String msg);

	/**
	 * 玩家发送私聊消息
	 *
	 * @param  roleId     发送者玩家 ID
	 * @param  receiverId 接收者玩家 ID
	 * @param  msg        消息文本
	 * @return            发送结果
	 */
	Result sendWhisper(int roleId, int receiverId, String msg);

	/**
	 * 登录回调。获取离线私聊消息等
	 *
	 * @param roleId 玩家 ID
	 */
	void login(int roleId);

	/**
	 * 消息验证
	 *
	 * @param  text 消息文本
	 * @return      结果
	 */
	Result checkMsg(String text);
}
