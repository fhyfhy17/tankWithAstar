package com.ourpalm.core.util;

public class ByteUtil {

	  public static byte[] copyOfRange(byte[] original, int from, int to) {
	        int newLength = to - from;
	        if (newLength < 0) {
	            throw new IllegalArgumentException(from + " > " + to);
	        }
	        byte[] copy = new byte[newLength];
	        System.arraycopy(original, from, copy, 0,
	                Math.min(original.length - from, newLength));
	        return copy;
	    }
	  
	  
	  public static  byte[] intToByte(int i) {
	       /* byte[] abyte0 = new byte[4];
	        abyte0[0] = (byte) (0xff & i);
	        abyte0[1] = (byte) ((0xff00 & i) >> 8);
	        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
	        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
	        return abyte0;*/
		 
		 byte[] result = new byte[4]; 
		 result[0] = (byte)((i >> 24) & 0xFF); 
		 result[1] = (byte)((i >> 16) & 0xFF); 
		 result[2] = (byte)((i >> 8) & 0xFF); 
		 result[3] = (byte)(i & 0xFF); 
		 return result; 

	    }

	 public static  int bytesToInt(byte[] bytes) {
	        /*int addr = bytes[0] & 0xFF;
	        addr |= ((bytes[1] << 8) & 0xFF00);
	        addr |= ((bytes[2] << 16) & 0xFF0000);
	        addr |= ((bytes[3] << 24) & 0xFF000000);
	        return addr;*/
		 int offset = 0 ;
		 int value = 0;
	        for (int i = 0; i < 4; i++) {
	            int shift = (4 - 1 - i) * 8;
	            value += (bytes[i + offset] & 0x000000FF) << shift;
	        }
	        return value;
	    }
	 
	 public static  int bytesToInt(byte[] bytes,int offset) {
		 //int offset = 0 ;
		 int value = 0;
	        for (int i = 0; i < 4; i++) {
	            int shift = (4 - 1 - i) * 8;
	            value += (bytes[i + offset] & 0x000000FF) << shift;
	        }
	        return value;
	    }
	 
	public static short bytesToShort(byte[] bytes) {
		int offset = 0 ;
		return (short)((bytes[offset] << 8) + (bytes[offset+1] & 0xFF));
	}
	
	public static short bytesToShort(byte[] bytes,int index) {
		return (short)((bytes[index] << 8) + (bytes[index+1] & 0xFF));
	}
	 
	 public static byte[] shortToBytes(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

}
