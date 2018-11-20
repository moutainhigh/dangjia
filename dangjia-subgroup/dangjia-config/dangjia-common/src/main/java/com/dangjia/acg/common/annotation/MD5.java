package com.dangjia.acg.common.annotation;

import java.security.MessageDigest;

/**
 * MD5算法
 * 
 * @author Qiyuxiang
 */
public class MD5 {
	public static void main(String[] args) {
		
		System.out.println(encode("123456"));
	}

	public static String encode(String source) {
		String res = null;
		try {
			res = new String(source);
			MessageDigest md = MessageDigest.getInstance("MD5");
			res = byte2hexString(md.digest(res.getBytes()));

		} catch (Exception ex) {
		}
		return res;
	}

	private static final String byte2hexString(byte[] bytes) {
		StringBuffer bf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				bf.append("0");
			}
			bf.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return bf.toString();
	}
	
	public static String convertMD5(String inStr){

		char[] a = inStr.toCharArray();

		for (int i = 0; i < a.length; i++){

		a[i] = (char) (a[i] ^ 't');

		}

		String s = new String(a);

		return s;

	}

}
