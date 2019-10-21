package com.ourpalm.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.RTSI;

/**
 * 
 * 扫描指定package中的Action
 *
 */
public class AutoLoadActionFactory implements ActionFactory<MessageLite>{

	private Map<String, Action<MessageLite>> actionMap = new HashMap<>();
	
	private List<String> pkgPathList;
	
	public void init(){
		Set<Class> actionClass = RTSI.findClass(pkgPathList, Action.class);
		for(Class<Action> clazz : actionClass){
			try{
				Command command = clazz.getAnnotation(Command.class);
				if(command == null){
					continue ;
				}
				Action<MessageLite> action = (Action)clazz.newInstance();
				String key = this.buildKey(command.type(), command.id());
				if(actionMap.containsKey(key)){
					LogCore.startup.error("Action 重复 type = {}, cmd = {}", command.type(), command.id());
					continue;
				}
				actionMap.put(key, action);
			}catch(Exception e){
				LogCore.startup.error("", e);
			}
		}
		LogCore.startup.info("加载Action size={}", actionMap.size());
	}
	
	
	private String buildKey(int type, int id){
		return type + Cat.colon + id;
	}
	

	public void setPkgPathList(List<String> pkgPathList) {
		this.pkgPathList = pkgPathList;
	}


	@Override
	public Action<MessageLite> getAction(int cmdType, int cmdId) {
		Action<MessageLite> action = actionMap.get(this.buildKey(cmdType, cmdId));
		if(action == null){
			LogCore.runtime.warn("======== 没有对应的处理逻辑Action ====== cmdType:{}  cmdId:{}", cmdType, cmdId);
			return null;
		}
		return action;
	}


	public static void main(String[] args){
		AutoLoadActionFactory factory = new AutoLoadActionFactory();
		List<String> pkgPathList = new ArrayList<>();
		pkgPathList.add("com.ourpalm.tank.action.role");
		factory.setPkgPathList(pkgPathList);
		
		factory.init();
	}
	
	
}
