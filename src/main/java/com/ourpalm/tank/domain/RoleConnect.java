package com.ourpalm.tank.domain;

import com.google.protobuf.GeneratedMessageLite;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.Message;

/*
 * 此对象需要在进程中缓存
 * 避免多点登录用户被踢下线，redis中的连接对象已更最新
 * 而消息队列还存在未发送完的消息发送到更新后的IO链接上
 */
public class RoleConnect {

	private int roleId;			//角色ID
	private int areaId;			//分区id
	private int ioId;			//socket IO编号
	private String gateName;	//链接所在节点名
	private String nodeName; 	//当前所在节点
	private long hearbeatTime = System.currentTimeMillis();  //上次接受到的心跳时间
	private long openKeyTime = System.currentTimeMillis();  //登录后，需要对 openKey 做时效延续
	
	/** 向客户端主推消息 */
	public void sendMsg(int cmdType, int cmdId, byte[] data){
		RemoteNode remote = GameContext.getGatewayManagerApp().getRemoteNode(gateName);
		if(remote == null){
			LogCore.runtime.warn("网关节点 {} 不存在或者已经挂了", gateName);
			return ;
		}
		Message pushMsg = new Message();
		pushMsg.setCmdId((byte)cmdId);
		pushMsg.setCmdType((byte)cmdType);
		pushMsg.setData(data);
		pushMsg.setIoId(ioId);
		pushMsg.setFromNode(GameContext.getLocalNodeName());
		
		remote.sendReqMsg(pushMsg);
	}
	
	
	/** 向客户端主推消息 */
	public void sendMsg(GeneratedMessageLite message){
		int[] cmds = GameContext.getMsgParser().clazzToId(message.getClass());
		if(cmds == null){
			LogCore.runtime.error("消息对应的ID未配置，Message={}", message.getClass().getSimpleName());
			return;
		}
		this.sendMsg(cmds[0], cmds[1], message.toByteArray());
	}
	
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getIoId() {
		return ioId;
	}
	public void setIoId(int ioId) {
		this.ioId = ioId;
	}
	public String getGateName() {
		return gateName;
	}
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public long getHearbeatTime() {
		return hearbeatTime;
	}
	public void setHearbeatTime(long hearbeatTime) {
		this.hearbeatTime = hearbeatTime;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public long getOpenKeyTime() {
		return openKeyTime;
	}
	public void setOpenKeyTime(long openKeyTime) {
		this.openKeyTime = openKeyTime;
	}
}
