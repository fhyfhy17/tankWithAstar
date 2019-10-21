package com.ourpalm.core.node.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.ZkClient;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;

public class RemoteManagerAppImpl implements RemoteManagerApp{

	// KEY<名称,远程节点对象>
	private Map<String, RemoteNode> remoteMap = new ConcurrentHashMap<String, RemoteNode>();
	// 节点名称List
	private List<String> remoteList = new ArrayList<String>();
	
	private ZkClient zkClient;
	// 所注册的服务器根节点名称
	private String registerServerNodesName;
	
	//分配规则轮询id
	private AtomicInteger loopId = new AtomicInteger(0);
	
	//本地节点名称
	private String localNode;
	
	@Override
	public void start() {
		this.localNode = GameContext.getLocalNodeName();
		
		LogCore.startup.info("[ZooKeeper]开始向注册中心获取远程服务器列表...");
		if(!zkClient.exists(registerServerNodesName)){
			zkClient.createPersistent(registerServerNodesName);
			LogCore.startup.info("...不存在根节点{}，创建完毕", registerServerNodesName);
		}
		
		//监听节点
		zkClient.subscribeChildChanges(registerServerNodesName, new IZkChildListener(){
			@Override
			public void handleChildChange(String paramString, List<String> paramList) throws Exception {
				LogCore.startup.info("[ZooKeeper]注册中心远程服务器节点发生变化...");
				//重新构建远程节点
				remoteList = paramList;
				rebuildRemoteNodeMap(paramList);
			}
		});
		
		List<String> list = zkClient.getChildren(registerServerNodesName);
		if(Util.isEmpty(list)){
			LogCore.startup.warn("...目前没有注册的远程服务器");
			return ;
		}
		
		//创建远程节点列表
		this.remoteList = list;
		this.initRemoteNodeMap(list);
	}
	
	
	// 初始化远程节点Map
	private void initRemoteNodeMap(List<String> remoteNames){
		if(Util.isEmpty(remoteNames)){
			LogCore.startup.warn("...目前没有注册的远程服务器");
			return ;
		}
		
		for(String nodeName : remoteNames){
			if(nodeName.equals(this.localNode)){
				continue;
			}
			RemoteNode remote = this.createRemoteNode(nodeName);
			remoteMap.put(nodeName, remote);
			LogCore.startup.info("发现 {} 节点，并注册完成.", nodeName);
		}
	}
	
	
	// 更新远程节点Map
	private void rebuildRemoteNodeMap(List<String> newNodeNames){
		// 新节点为空，表示全挂了。
		if(Util.isEmpty(newNodeNames)){
			this.clearAllRemoteNode();
			this.remoteMap.clear();
			return ;
		}
		
		//存在的取出来缓存，不存在的新建
		Map<String, RemoteNode> newRemoteMap = new ConcurrentHashMap<String, RemoteNode>();
		for(String newNode : newNodeNames){
			if(newNode.equals(this.localNode)){
				continue;
			}
			RemoteNode remote = this.remoteMap.remove(newNode);
			if(remote == null){
				remote = this.createRemoteNode(newNode);
			}
			newRemoteMap.put(newNode, remote);
		}
		//关闭旧列表
		this.clearAllRemoteNode();
		
		//按新的列表置换旧列表
		this.remoteMap = newRemoteMap;
	}
	
	
	
	// 创建新的远程节点
	private RemoteNode createRemoteNode(String nodeName){
		RemoteNode remote = new RemoteNode(nodeName);
		remote.startup();
		return remote;
	}
	
	
	// 关闭并清除所有节点的连接
	private void clearAllRemoteNode(){
		for(RemoteNode remote : this.remoteMap.values()){
			LogCore.startup.info("将关闭远程节点 nodeName = {}", remote.getRemoteAddr());
			remote.stop();
		}
		this.remoteMap.clear();
	}
	
	
	@Override
	public RemoteNode getRemoteNode(String remoteName){
		if(Util.isEmpty(remoteName)){
			return null;
		}
		return remoteMap.get(remoteName);
	}
	
	
	// 轮询分配远程服务器
	@Override
	public String loopRemoteNodeName(){
		if(Util.isEmpty(remoteList)){
//			throw new RuntimeException("当前没有找到任何远程服务节点...");
			return null;
		}
		int id = this.loopId.incrementAndGet();
		if(id < 0){
			this.loopId.set(0);
			id = 0;
		}
		int random = id % this.remoteList.size();
		return this.remoteList.get(random);
	}
	
	
	@Override
	public RemoteNode randomRemoteNode(){
		String randomNode = this.loopRemoteNodeName();
		return this.getRemoteNode(randomNode);
	}
	

	@Override
	public void stop() {
		this.clearAllRemoteNode();
	}

	public void setZkClient(ZkClient zkClient) {
		this.zkClient = zkClient;
	}

	public void setRegisterServerNodesName(String registerServerNodesName) {
		this.registerServerNodesName = registerServerNodesName;
	}


	@Override
	public Collection<RemoteNode> getAllRemoteNode() {
		return remoteMap.values();
	}
}
