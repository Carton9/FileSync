package com.net;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DECKey implements KeyUnit {
	private static String testKey="3356360843822592";
	private String key;
	public DECKey(String key) {
		this.key=key;
	}
	public static DECKey getdefultKey() {
		return new DECKey(testKey);
	}
	@Override
	public int decryptSize(int originSize) {
		// TODO Auto-generated method stub
		return (originSize/8+1)*8;
	}

	@Override
	public int encryptSize(int encryptionSize) {
		// TODO Auto-generated method stub
		return (encryptionSize/8-1)*8;
	}

	@Override
	public byte[] decrypt(byte[] in) {
		// TODO Auto-generated method stub
		return decrypt(in,key);
	}

	@Override
	public byte[] encrypt(byte[] in) {
		// TODO Auto-generated method stub
		return encrypt(in,key);
	}
	 public static byte[] encrypt(byte[] content, String key) {  
	        try {  
	            SecureRandom random = new SecureRandom();  
	            DESKeySpec desKey = new DESKeySpec(key.getBytes());  
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
	            SecretKey securekey = keyFactory.generateSecret(desKey);  
	            Cipher cipher = Cipher.getInstance("DES");  
	            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);  
	            byte[] result = cipher.doFinal(content);  
	            return result;  
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	 public static byte[] decrypt(byte[] content, String key) {  
	        try {  
	            SecureRandom random = new SecureRandom();  
	            DESKeySpec desKey = new DESKeySpec(key.getBytes());  
	            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
	            SecretKey securekey = keyFactory.generateSecret(desKey);  
	            Cipher cipher = Cipher.getInstance("DES");  
	            cipher.init(Cipher.DECRYPT_MODE, securekey, random);  
	            byte[] result = cipher.doFinal(content);  
	            return result;  
	        } catch (Throwable e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
}
