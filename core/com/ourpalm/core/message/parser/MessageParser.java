package com.ourpalm.core.message.parser;

import com.google.protobuf.MessageLite;

public interface MessageParser {

	/** 通过消息获取消息类型、消息命令id */
	int[] clazzToId(Class<? extends MessageLite> clazz);
	
	/** 解析具体消息对象 */
	MessageLite parser(int cmdType, int id, byte[] data) throws Exception;
}
