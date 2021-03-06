package com.ourpalm.core.util;

import static com.ourpalm.core.util.WireFormat.ARRAY;
import static com.ourpalm.core.util.WireFormat.BOOLEAN;
import static com.ourpalm.core.util.WireFormat.BYTE;
import static com.ourpalm.core.util.WireFormat.COLLECTION;
import static com.ourpalm.core.util.WireFormat.DISTRIBUTED;
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.protobuf.CodedOutputStream;

public class OutputStream {
	
	private CodedOutputStream stream;		//字节流处理类
	
	private int lengthTotal;				//字节流最大长度
	private byte[] buffer;					//字节流数据
	
	public OutputStream() {
		this(BufferPool.allocate());
	}
	
	public OutputStream(byte[] buffer) {
		this.lengthTotal = buffer.length;
		this.buffer = buffer;
		
		this.stream = CodedOutputStream.newInstance(buffer);
	}
	
	/**
	 * 重置
	 */
	public void reset() {
		stream = CodedOutputStream.newInstance(buffer);
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		BufferPool.deallocate(buffer);
		buffer = null;
		stream = null;
	}
	
	/**
	 * 获取流的已使用长度
	 * @return
	 */
	public int getLength() {
		return lengthTotal - stream.spaceLeft();
	}
	
	/**
	 * 获取流实际的byte数据
	 * @return
	 */
	public byte[] getBuffer() {
		return buffer;
	}
	
	
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 * @throws IOException
	 */
	public void write(Object value) {
		try {
			writeObject(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 * @throws IOException
	 */
	private void writeObject(Object value) throws IOException {
		//空对象
		if(value == null) {
			stream.writeInt32NoTag(NULL);
			return;
		}
		
		//数据类型
		Class<?> clazz = value.getClass();

		//BYTE
		if(clazz == byte.class || clazz == Byte.class) {
			stream.writeInt32NoTag(BYTE);
			stream.writeRawByte((byte)value);
		} else if(clazz == byte[].class) {
			byte[] array = (byte[])value;
			stream.writeInt32NoTag(BYTE | ARRAY);
			stream.writeInt32NoTag(array.length);
			stream.writeRawBytes(array);
		//BOOLEAN
		} else if(clazz == boolean.class || clazz == Boolean.class) {
			stream.writeInt32NoTag(BOOLEAN);
			stream.writeBoolNoTag((boolean)value);
		} else if(clazz == boolean[].class) {
			boolean[] array = (boolean[])value;
			stream.writeInt32NoTag(BOOLEAN | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeBoolNoTag(array[i]);
			}
		//INT
		} else if(clazz == int.class || clazz == Integer.class) {
			stream.writeInt32NoTag(INT);
			stream.writeInt32NoTag((int)value);
		} else if(clazz == int[].class) {
			int[] array = (int[])value;
			stream.writeInt32NoTag(INT | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeInt32NoTag(array[i]);
			}
		//LONG
		} else if(clazz == long.class || clazz == Long.class) {
			stream.writeInt32NoTag(LONG);
			stream.writeInt64NoTag((long)value);
		} else if(clazz == long[].class) {
			long[] array = (long[])value;
			stream.writeInt32NoTag(LONG | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeInt64NoTag(array[i]);
			}
		//FLOAT
		} else if(clazz == float.class || clazz == Float.class) {
			stream.writeInt32NoTag(FLOAT);
			stream.writeFloatNoTag((float)value);
		} else if(clazz == float[].class) {
			float[] array = (float[])value;
			stream.writeInt32NoTag(FLOAT | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeFloatNoTag(array[i]);
			}
		//DOUBLE
		} else if(clazz == double.class || clazz == Double.class) {
			stream.writeInt32NoTag(DOUBLE);
			stream.writeDoubleNoTag((double)value);
		} else if(clazz == double[].class) {
			double[] array = (double[])value;
			stream.writeInt32NoTag(DOUBLE | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeDoubleNoTag(array[i]);
			}
		//STRING
		} else if(clazz == String.class) {
			stream.writeInt32NoTag(STRING);
			stream.writeStringNoTag((String)value);
		} else if(clazz == String[].class) {
			String[] array = (String[])value;
			stream.writeInt32NoTag(STRING | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeStringNoTag(array[i]);
			}
		//ENUM
		} else if(value instanceof Enum) {
			Enum<?> val = (Enum<?>) value;
			stream.writeInt32NoTag(ENUM);
			stream.writeStringNoTag(val.getClass().getName());
			stream.writeStringNoTag(val.name());
			
		//COLLECTION LIST SET
		} else if(value instanceof Collection) {
			Collection<?> val = (Collection<?>) value;
			
			//判断子类型
			int type;
			if(value instanceof List) type = LIST;
			else if(value instanceof Set) type = SET;
			else type = COLLECTION;
			
			stream.writeInt32NoTag(type);
			stream.writeInt32NoTag(val.size());
			
			for(Object o : val) {
				this.writeObject(o);
			}
		//MAP
		} else if(value instanceof Map) {
			Map<?,?> val = (Map<?,?>) value;
			
			stream.writeInt32NoTag(MAP);
			stream.writeInt32NoTag(val.size());
			
			for(Entry<?, ?> e : val.entrySet()) {
				Object k = e.getKey();
				Object v = e.getValue();
				
				this.writeObject(k);
				this.writeObject(v);
			}
		//数组
		} else if(value instanceof Object[]) {
			Object[] array = (Object[])value;
			stream.writeInt32NoTag(OBJECT | ARRAY);
			stream.writeInt32NoTag(array.length);
			for(Object o : array) {
				this.writeObject(o);
			}
		
		}else if(value instanceof ISerilizable) {
			ISerilizable seriable = (ISerilizable)value;
			stream.writeInt32NoTag(DISTRIBUTED);
			stream.writeInt32NoTag(seriable.serilizableId());
			seriable.writeTo(this);
		
		//其余一律不支持
		} else {
			throw new RuntimeException("发现无法被Distributed序列化的类型:" + clazz.getName());
		}
	}
	
	/**
	 * 写入byte[]
	 * @param buf
	 * @param offset
	 * @param length
	 * @throws IOException
	 */
	public void writeBytes(byte[] buf, int offset, int length) throws IOException {
		stream.writeInt32NoTag(BYTE | ARRAY);
		stream.writeInt32NoTag(length);
		stream.writeRawBytes(buf, offset, length);
	}
}