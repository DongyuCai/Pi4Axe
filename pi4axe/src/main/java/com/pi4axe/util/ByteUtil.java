package com.pi4axe.util;

import java.nio.ByteBuffer;

public final class ByteUtil {
	
	public static byte[] hexStr2Byte(String hex) {
	    ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
	    for (int i = 0; i < hex.length(); i++) {
	        String hexStr = hex.charAt(i) + "";
	        i++;
	        hexStr += hex.charAt(i);
	        byte b = (byte) Integer.parseInt(hexStr, 16);
	        bf.put(b);
	    }
	    return bf.array();
	}
	
}
