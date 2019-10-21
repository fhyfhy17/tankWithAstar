package com.ourpalm.core.node;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.OutputStream;
import com.ourpalm.tank.message.Message;

public class RemoteNode {
	
	private String remoteAddr;		//需连接的远程节点地址(ip:port)
	
	private ZContext zmqContext ;
	
	private ZMQ.Socket zmqPush;
	
	
	public RemoteNode(String remoteAddr){
		this.remoteAddr = remoteAddr;
	}
	
	
	public void startup(){
		try{
			LogCore.startup.info("[启动Remote]节点 {} [建立连接][启动分发线程]...", remoteAddr);
			
			zmqContext = new ZContext();
			zmqContext.setIoThreads(1);
			
			zmqPush = zmqContext.createSocket(ZMQ.PUSH);
			zmqPush.setLinger(3000);
			zmqPush.setSndHWM(0);
			zmqPush.connect("tcp://" + remoteAddr);
			
	        LogCore.startup.info("...[建立连接]连接建立完毕");
	        
		}catch(Exception e){
			LogCore.startup.error("", e);
		}
	}
	
	
	public void sendReqMsg(Message message){
		synchronized(this){
			OutputStream output = new OutputStream();
			output.write(message);
			zmqPush.send(output.getBuffer(), 0, output.getLength(), 0);
			output.close();
		}
	}
	
	
	public void stop(){
		try {
			this.zmqContext.destroy();
		} catch (Exception e) {
			LogCore.startup.info("{} 节点close", this.remoteAddr, e);
		}
	}


	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	
}
