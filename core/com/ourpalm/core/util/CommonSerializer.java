package com.ourpalm.core.util;

import com.ourpalm.tank.message.Message;

public class CommonSerializer {

	public static ISerilizable create(int id){
		switch(id){
			case ISerilizable.MESSAGE_SERILIZABLE_ID : return new Message();
		}
		throw new RuntimeException("序列化id = " + id +" 没有对应的实例化对象");
	}
	
}
