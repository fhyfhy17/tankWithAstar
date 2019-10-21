package com.ourpalm.core.util;

import static com.ourpalm.core.util.WireFormat.ARRAY;
import static com.ourpalm.core.util.WireFormat.BOOLEAN;
import static com.ourpalm.core.util.WireFormat.BYTE;
import static com.ourpalm.core.util.WireFormat.COLLECTION;
import static com.ourpalm.core.util.WireFormat.DOUBLE;
import static com.ourpalm.core.util.WireFormat.ENUM;
import static com.ourpalm.core.util.WireFormat.FLOAT;
import static com.ourpalm.core.util.WireFormat.INT;
import static com.ourpalm.core.util.WireFormat.LIST;
import static com.ourpalm.core.util.WireFormat.LONG;
import static com.ourpalm.core.util.WireFormat.MAP;
import static com.ourpalm.core.util.WireFormat.NULL;
import static com.ourpalm.core.util.WireFormat.OBJECT;
import static com.ourpalm.core.util.WireFormat.SET;
import static com.ourpalm.core.util.WireFormat.STRING;
import static com.ourpalm.core.util.WireFormat.DISTRIBUTED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.CodedInputStream;

public class InputStream {
	private final CodedInputStream stream;		//字节流处理类
	
	public InputStream(byte[] buffer, int offset, int length) {
		this.stream = CodedInputStream.newInstance(buffer, offset, length);
	}
	
	/**
	 * 是否已全部读取完毕
	 * @return
	 */
	public boolean isAtEnd() {
		try {
			return stream.isAtEnd();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	public <T> T read() {
		try {
			return readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T readObject() throws Exception {
		Object result = null;

		//类型码
		int wireFormat = stream.readInt32();
		//类型
		int wireType = (wireFormat & ~ARRAY);
		//是数组类型
		boolean isArray = (wireFormat & ARRAY) == ARRAY;
		//数组类型的长度
		int arrayLen = 0;
		if(isArray) {
			arrayLen = stream.readInt32();
		}
		
		//空对象
		if(wireType == NULL) {
			return null;
		}
		
		//BYTE
		if(wireType == BYTE) {
			if(isArray) {
				result = stream.readRawBytes(arrayLen);
			} else {
				result = stream.readRawByte();
			}
		//BOOLEAN
		} else if(wireType == BOOLEAN) {
			if(isArray) {
				boolean[] values = new boolean[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readBool();
				}
				result = values;
			} else {
				result = stream.readBool();
			}
		//INT
		} else if(wireType == INT) {
			if(isArray) {
				int[] values = new int[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readInt32();
				}
				result = values;
			} else {
				result = stream.readInt32();
			}
		//LONG
		} else if(wireType == LONG) {
			if(isArray) {
				long[] values = new long[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readInt64();
				}
				result = values;
			} else {
				result = stream.readInt64();
			}
		//FLOAT
		} else if(wireType == FLOAT) {
			if(isArray) {
				float[] values = new float[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readFloat();
				}
				result = values;
			} else {
				result = stream.readFloat();
			}
		//DOUBLE
		} else if(wireType == DOUBLE) {
			if(isArray) {
				double[] values = new double[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readDouble();
				}
				result = values;
			} else {
				result = stream.readDouble();
			}
		//STRING
		} else if(wireType == STRING) {
			if(isArray) {
				String[] values = new String[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readString();
				}
				result = values;
			} else {
				result = stream.readString();
			}
		//ENUM
		} else if(wireType == ENUM) {
			//实际类型
			String className = stream.readString();
			String val = stream.readString();
			
			//创建实例
			Class cls = Class.forName(className);
			result = Enum.valueOf(cls, val);
		
		//COLLECTION LIST SET
		} else if(wireType == COLLECTION || wireType == LIST || wireType == SET) {
			//长度
			int len = stream.readInt32();
			
			//类型
			Collection list;
			if(wireType == LIST) list = new ArrayList<>();
			else if(wireType == SET) list = new HashSet<>();
			else list = new ArrayList<>();	//未知Collection的具体实现 暂时一律使用arrayList子类的实现
			
			//填充数据
			for(int i = 0; i < len; i++) {
				list.add(this.read());
			}
			
			result = list;
						
		//MAP
		} else if(wireType == MAP) {
			//长度
			int len = stream.readInt32();
			
			//数据
			Map map = new LinkedHashMap<>();
			for(int i = 0; i < len; i++) {
				Object key = this.read();
				Object val = this.read();
				
				map.put(key, val);
			}
			
			result = map;
		//Object[]
		} else if(wireType == OBJECT && isArray) {
			Object[] values = new Object[arrayLen];
			for(int i = 0; i < arrayLen; i++) {
				values[i] = this.read();
			}
			result = values;
		} else if(wireType == DISTRIBUTED) {
			//实际类型
			int id = stream.readInt32();
			ISerilizable seriable = CommonSerializer.create(id);
			seriable.readFrom(this);
			result = seriable;
		//其余一律不支持
		} else {
			throw new RuntimeException("发现无法被Distributed反序列化的类型: wireType=" + wireType + ", isArray="+ isArray);
		}
		
		//返回值
		return (T) result;
	}
}
