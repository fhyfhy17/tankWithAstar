package com.ourpalm.core.action;

import com.google.protobuf.MessageLite;

public class ActionContext {
	
	
	private byte cmdType;
	private byte cmdId;
	private int ioId;
	private String from ;
	private MessageLite reqMsg;
	private Action<MessageLite> action;
	
	private ActionContext(){}
	
	
	public static ActionContext build(){
		return new ActionContext();
	}

	public byte getCmdType() {
		return cmdType;
	}

	public void setCmdType(byte cmdType) {
		this.cmdType = cmdType;
	}

	public byte getCmdId() {
		return cmdId;
	}

	public void setCmdId(byte cmdId) {
		this.cmdId = cmdId;
	}

	public int getIoId() {
		return ioId;
	}

	public void setIoId(int ioId) {
		this.ioId = ioId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public MessageLite getReqMsg() {
		return reqMsg;
	}


	public void setReqMsg(MessageLite reqMsg) {
		this.reqMsg = reqMsg;
	}


	public Action<MessageLite> getAction() {
		return action;
	}

	public void setAction(Action<MessageLite> action) {
		this.action = action;
	}

}
