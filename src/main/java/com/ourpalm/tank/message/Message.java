package com.ourpalm.tank.message;

import java.io.IOException;

import com.ourpalm.core.util.ISerilizable;
import com.ourpalm.core.util.InputStream;
import com.ourpalm.core.util.OutputStream;

public class Message implements ISerilizable{
	private int ioId;		//连接IO
	private byte cmdType;	//协议类型
	private byte cmdId;		//协议号
	private byte[] data;	//协议内容
	private String fromNode; //来自节点名
	
	
	@Override
	public int serilizableId() {
		return ISerilizable.MESSAGE_SERILIZABLE_ID;
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(ioId);
		out.write(cmdType);
		out.write(cmdId);
		out.write(data);
		out.write(fromNode);
	}
	@Override
	public void readFrom(InputStream in) throws IOException {
		ioId = in.read();
		cmdType = in.read();
		cmdId = in.read();
		data = in.read();
		fromNode = in.read();
	}
	
	
	public int getIoId() {
		return ioId;
	}
	public void setIoId(int ioId) {
		this.ioId = ioId;
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
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getFromNode() {
		return fromNode;
	}
	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}
}
