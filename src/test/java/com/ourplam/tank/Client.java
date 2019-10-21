package com.ourplam.tank;

import java.io.ByteArrayOutputStream;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.protobuf.CodedOutputStream;
import com.ourpalm.tank.util.RandomUtil;

public class Client {

	public static void main(String[] args) throws Exception{
		
		String addr = "tcp://127.0.0.1:9999";
		
		ZContext zmqContext = new ZContext();
		zmqContext.setIoThreads(1);
		
		ZMQ.Socket zmqPush = zmqContext.createSocket(ZMQ.PUSH);
		zmqPush.setSndHWM(0);
		zmqPush.setLinger(3000);
		zmqPush.connect(addr);
			
		while(true){
			zmqPush.send("hello "+ RandomUtil.randomInt());
			System.out.println("send hello");
			Thread.sleep(10);
		}
		
		
//		zmqContext.close();
//		zmqContext.destroy();
	}
}
