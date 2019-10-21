package com.ourpalm.core.message.handler;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.ActionFactory;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.message.parser.MessageParser;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.node.manager.RemoteManagerApp;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.ROLE_MSG.STC_EXCEP_OFFLINE_MSG;

public class MessageThreadHandler extends Thread implements MessageHandler{

	private Logger logger = LogCore.runtime;
	
	//执行器ID
	private String id;
	//心跳频率10毫秒
	private int interval = 10;
	
	private ActionFactory<MessageLite> actionFactory;
	
	private MessageParser msgParser;
	
	private RemoteManagerApp remoteManagerApp;
	
	private final ConcurrentLinkedQueue<Message> pulseQueues = new ConcurrentLinkedQueue<>();
	
	private volatile boolean running = false;
	
	
	public MessageThreadHandler(String id){
		this.id = id;
	}
	
	
	@Override
	public void startup() {
		//正在运行
		if(running){
			return ;
		}
		
		//开始启动
		running = true;
		
		start();
	}
	
	
	@Override
	public void run() {
		while(running){
			long startTime = System.currentTimeMillis();
			
			//执行心跳
			pulse();
			
			//统计时长
			long finishTime = System.currentTimeMillis();	
			long timeRunning = finishTime - startTime;
			if(timeRunning > 100){
				LogCore.system.warn("执行器 id={}, 本次心跳时长较长,达到 time={},当前队列大小 Size={}",
						id, timeRunning, pulseQueues.size());
			}
			try {
				if(timeRunning < interval) {
					Thread.sleep(interval - timeRunning);
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	

	@Override
	public void messageReceived(Message msg) {
		pulseQueues.add(msg);
	}
	
	
	
	//心跳操作
	private void pulse(){
		while(!pulseQueues.isEmpty()){
			Message msg = pulseQueues.poll();
			try{
				Action<MessageLite> action = actionFactory.getAction(msg.getCmdType(), msg.getCmdId());
				if(action == null){
					logger.warn("=========cmdType:{} cmdId:{} 没有对应的Action  ========", msg.getCmdType(), msg.getCmdId());
					continue ;
				}
				MessageLite msgLite = msgParser.parser(msg.getCmdType(), msg.getCmdId(), msg.getData());
				ActionContext context = ActionContext.build();
				context.setFrom(msg.getFromNode());
				context.setIoId(msg.getIoId());
				
				MessageLite resp = action.execute(context, msgLite);
				if(resp == null){
					continue ;
				}
				int[] cmds = msgParser.clazzToId(resp.getClass());
				Message pushMsg = new Message();
				pushMsg.setCmdType((byte)cmds[0]);
				pushMsg.setCmdId((byte)cmds[1]);
				pushMsg.setData(resp.toByteArray());
				pushMsg.setIoId(context.getIoId());
				pushMsg.setFromNode(GameContext.getLocalNodeName());
				
				String from = context.getFrom();
				RemoteNode remote = remoteManagerApp.getRemoteNode(from);
				if(remote == null){
					logger.error("远程{}节点不存在", from);
					continue ;
				}
				remote.sendReqMsg(pushMsg);
			}catch(InvalidProtocolBufferException ex){
				logger.error("", ex);
				RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(msg.getIoId());
				if(connect != null){
					STC_EXCEP_OFFLINE_MSG offlineMsg = STC_EXCEP_OFFLINE_MSG.newBuilder()
							.setTips("网络异常断开,请检查网络...")
							.build();
					connect.sendMsg(offlineMsg);
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}

	public void setActionFactory(ActionFactory<MessageLite> actionFactory) {
		this.actionFactory = actionFactory;
	}

	public void setMsgParser(MessageParser msgParser) {
		this.msgParser = msgParser;
	}

	public void setRemoteManagerApp(RemoteManagerApp remoteManagerApp) {
		this.remoteManagerApp = remoteManagerApp;
	}
}
