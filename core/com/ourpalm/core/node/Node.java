package com.ourpalm.core.node;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.message.handler.MessageHandler;
import com.ourpalm.core.service.Service;
import com.ourpalm.core.util.InputStream;
import com.ourpalm.core.util.NetUtil;
import com.ourpalm.tank.message.Message;

public class Node implements Service {

	private String host;
	private int port;

	// 消息处理逻辑
	private MessageHandler messageHandler;

	private ZkClient zkClient;

	private ZContext zmqContext;

	private ZMQ.Socket zmqPull;

	private volatile boolean running = false;

	private int interval = 10;

	// 注册节点
	private String registerServerNodesName;

	@Override
	public void start() {
		if (running) {
			return;
		}

		running = true;

		this.host = this.createNodeName();
		// 注册自身
		this.registerServer();
		// 启动分发线程
		messageHandler.startup();

		// 消息监听服务,此服务为阻塞服务最后调用
		this.init();
	}

	@Override
	public void stop() {
		running = false;
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		zmqContext.destroy();
	}

	// 初始化消息监听服务
	private void init() {
		try {

			LogCore.startup.info("[启动Node]节点 {} ...[监听节点]", host);

			zmqContext = new ZContext();
			zmqContext.setIoThreads(1);

			zmqPull = zmqContext.createSocket(ZMQ.PULL);
			zmqPull.setLinger(3000);
			zmqPull.setRcvHWM(0);
			zmqPull.bind("tcp://" + host);

			LogCore.startup.info("...[监听节点]完毕");

			while (running) {
				try {
					long startTime = System.currentTimeMillis();

					pulse();

					long timeRunning = System.currentTimeMillis() - startTime;
					if (timeRunning < interval) {
						Thread.sleep(interval - timeRunning);
					} else {
						Thread.sleep(1);
					}
				} catch (Exception e) {
					LogCore.runtime.error("加入队列异常", e);
				}
			}
		} catch (Exception e) {
			LogCore.startup.error("", e);
		}
	}

	private void pulse() {
		while (running) {
			byte[] buf = null;
			try {
				buf = zmqPull.recv();
			} catch (Exception e) {
				LogCore.runtime.error(" zmqPull 已停止");
			}
			if (buf == null || buf.length <= 0) {
				break;
			}
			InputStream input = new InputStream(buf, 0, buf.length);
			Message msg = input.read();
			messageHandler.messageReceived(msg);

		}
	}

	// 启动消息处理线程
	private void registerServer() {
		try {
			LogCore.startup.info("[ZooKeeper]开始向注册中心注册Node节点...[注册子节点]");
			if (!zkClient.exists(registerServerNodesName)) {
				zkClient.createPersistent(registerServerNodesName);
				LogCore.startup.info("...不存在{}根节点，创建完毕", registerServerNodesName);
			}

			final String childNode = this.registerServerNodesName + "/" + this.host;
			if (zkClient.exists(childNode)) {
				zkClient.delete(childNode);
			}
			zkClient.createEphemeral(childNode);

			LogCore.startup.info("...[注册子节点]{}完毕", childNode);

			// 重连上是重新注册节点
			zkClient.subscribeStateChanges(new IZkStateListener() {
				@Override
				public void handleStateChanged(KeeperState state) throws Exception {
				}

				@Override
				public void handleNewSession() throws Exception {
					if (!zkClient.exists(childNode)) {
						zkClient.createEphemeral(childNode);
						LogCore.startup.info("...[注册子节点]{}完毕", childNode);
					}
				}

			});

		} catch (Exception e) {
			LogCore.startup.error("", e);
		}
	}

	private String createNodeName() {
		StringBuffer hostName = new StringBuffer();
		hostName.append(NetUtil.getHostIp());
		hostName.append(":");
		hostName.append(this.port);

		return hostName.toString();
	}

	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void setRegisterServerNodesName(String registerServerNodesName) {
		this.registerServerNodesName = registerServerNodesName;
	}

	public void setZkClient(ZkClient zkClient) {
		this.zkClient = zkClient;
	}
}
