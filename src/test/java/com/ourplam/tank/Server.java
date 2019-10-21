package com.ourplam.tank;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server {

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;		//缓冲包2M
//	private static final byte[] buf = new byte[BUFFER_SIZE];
	
	public static void main(String[] args) throws Exception{
		String addr = "tcp://127.0.0.1:9999";
		
//		String addr = "tcp://192.168.70.159:20200";
		
		ZContext zmqContext = new ZContext();
		zmqContext.setIoThreads(1);
		
		ZMQ.Socket zmqPull = zmqContext.createSocket(ZMQ.PULL);
//		zmqPull.setRcvHWM(0);
		zmqPull.bind(addr);
		
		while (true) {
//			byte[] buf = BufferPool.allocate();
			try {
				byte[] buf = new byte[1024];
				int recvLen = zmqPull.recv(buf, 0, buf.length, ZMQ.DONTWAIT);
				if (recvLen <= 0) {
					Thread.sleep(1);
					continue;
				}
				System.out.println("recvLen = " + recvLen);
//				byte[] buf = zmqPull.recv();
//				if(buf == null || buf.length <= 0){
//					Thread.sleep(1);
//					continue;
//				}
				String message = new String(buf);
				System.out.println("message = " + message);
//				Thread.sleep(10);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
//				BufferPool.deallocate(buf);
			}
        }  
	}
}
