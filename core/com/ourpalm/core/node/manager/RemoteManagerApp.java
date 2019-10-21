package com.ourpalm.core.node.manager;

import java.util.Collection;

import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.service.Service;

public interface RemoteManagerApp extends Service{
	
	String loopRemoteNodeName();
	
	RemoteNode getRemoteNode(String remoteName);
	
	RemoteNode randomRemoteNode();
	
	Collection<RemoteNode> getAllRemoteNode();
}
