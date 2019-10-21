package com.ourpalm.core.message.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.ActionFactory;
import com.ourpalm.core.message.parser.MessageParser;
import com.ourpalm.core.node.manager.RemoteManagerApp;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.Message;

public class NodeMessageHandler implements MessageHandler{

	private ActionFactory<MessageLite> actionFactory;
	private MessageParser msgParser;
	private RemoteManagerApp remoteManagerApp;
	private volatile boolean running = false;
	private String cmds ; 					//指定执行命令
	private int handlerCount = 16;			//执行器数量
	private int defaultHandlerCount	= 8; 	//默认执行器数量
	
	private Set<String> cmdSet = new HashSet<String>();
	private List<MessageHandler>  hanlderList = new ArrayList<>();
	private List<MessageHandler>  defaultHanlderList = new ArrayList<>();
	
	@Override
	public void startup() {
		//正在运行
		if(running){
			return ;
		}
		
		//开始启动
		running = true;
		
		//初始化执行命令集
		if(!Util.isEmpty(cmds)){
			for(String cmd : cmds.split(Cat.comma)){
				if(Util.isEmpty(cmd)){
					continue;
				}
				cmdSet.add(cmd);
			}
		}
		
		//初始化hanlder
		this.initHandlers(hanlderList, handlerCount, "消息");
		this.initHandlers(defaultHanlderList, defaultHandlerCount, "默认消息");
	}
	
	
	private void initHandlers(List<MessageHandler> handlers, int count, String name){
		for(int i = 0; i < count; i++){
			MessageThreadHandler hanlder = new MessageThreadHandler(name + i);
			hanlder.setActionFactory(actionFactory);
			hanlder.setMsgParser(msgParser);
			hanlder.setRemoteManagerApp(remoteManagerApp);
			hanlder.startup();
			handlers.add(hanlder);
		}
	}

	@Override
	public void messageReceived(Message msg) {
		int key = msg.getIoId();
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(msg.getIoId());
		if(connect != null){
			key = connect.getRoleId();
		}
		//寻找执行器组
		List<MessageHandler> hanlders = this.findThreadHanlderList(msg);
		//分配执行器执行
		int index = Math.abs(key) % hanlders.size();
		MessageHandler hanlder = hanlders.get(index);
		hanlder.messageReceived(msg);
	}
	
	
	private List<MessageHandler> findThreadHanlderList(Message msg){
		if(cmdSet.contains(getKey(msg))){
			return hanlderList;
		}
		return defaultHanlderList;
	}
	
	
	private String getKey(Message message){
		return message.getCmdType() + Cat.underline + message.getCmdId();
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

	public void setCmds(String cmds) {
		this.cmds = cmds;
	}

	public void setHandlerCount(int handlerCount) {
		this.handlerCount = handlerCount;
	}

	public void setDefaultHandlerCount(int defaultHandlerCount) {
		this.defaultHandlerCount = defaultHandlerCount;
	}
}
