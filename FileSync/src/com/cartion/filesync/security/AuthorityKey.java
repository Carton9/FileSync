package com.cartion.filesync.security;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import com.carton.filesync.common.util.BiUnit;
import com.carton.filesync.common.util.SerializeUnit;

public class AuthorityKey extends SerializeUnit {
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private String Identity;
	private int number;
	private boolean isClient;
	private int containClient;
	private long[] seeds;
	private transient long currentSeed=56763432;
	transient static TransmissionEncryptionUtil encryption=new TransmissionEncryptionUtil();
	AuthorityKey(RSAPrivateKey privateKey,
	RSAPublicKey publicKey,
	String Identity,
	int number,
	boolean isClient,
	long[] seeds){
		this.privateKey=privateKey;
		this.publicKey=publicKey;
		this.Identity=Identity;
		this.number=number;
		this.isClient=isClient;
		this.seeds=seeds;
	}
	public static AuthorityKey makeServerKey() {
		BiUnit<RSAPublicKey,RSAPrivateKey>keypair=getKeyPair();
		SecureRandom random=new SecureRandom();
		String Identity=SHA512(System.currentTimeMillis()+""+random.nextLong());
		long seeds[]=new long[4];
		for(int i=0;i<4;i++) {
			seeds[i]=random.nextLong();
		}
		AuthorityKey authorityKey=new AuthorityKey(keypair.getO(),keypair.getK(),Identity,0,false,seeds);
		return authorityKey;
	}
	public static AuthorityKey makeClientKey(AuthorityKey serverKey) {
		SecureRandom random=new SecureRandom();
		for(int i=0;i<serverKey.containClient;i++) {
			serverKey.fakeRandom();
		}
		String Identity=SHA512(serverKey.Identity+""+serverKey.fakeRandom());
		serverKey.containClient++;
		AuthorityKey authorityKey=new AuthorityKey(null,serverKey.publicKey,Identity,0,true,null);
		return authorityKey;
	}
	public boolean verifyServer(byte id[]) {
		String privateKeyString = SHA512(new String(privateKey.getEncoded())); 
		String ids=new String(decrypt(id));
		if(privateKeyString.equals(ids))
			return true;
		return false;
	}
	public boolean verifyClient(String id) {
		washSeed();
		for(int i=0;i<containClient;i++) {
			if(SHA512(this.Identity+""+this.fakeRandom()).equals(id))
				return true;
		}
		return false;
	}
	public String getId() {
		return Identity;
	}
	private static String SHA512(String strText)  
	  {  
	    return SHA(strText, "SHA-512");  
	  }  
	private static String SHA(final String strText, final String strType)  
	  {  
	    String strResult = null;  
	    if (strText != null && strText.length() > 0)  
	    {  
	      try  
	      {  
	        MessageDigest messageDigest = MessageDigest.getInstance(strType);  
	        messageDigest.update(strText.getBytes());  
	        byte byteBuffer[] = messageDigest.digest();  
	        StringBuffer strHexString = new StringBuffer();  
	        for (int i = 0; i < byteBuffer.length; i++)  
	        {  
	          String hex = Integer.toHexString(0xff & byteBuffer[i]);  
	          if (hex.length() == 1)  
	          {  
	            strHexString.append('0');  
	          }  
	          strHexString.append(hex);  
	        }
	        strResult = strHexString.toString();  
	      }  
	      catch (NoSuchAlgorithmException e)  
	      {  
	        e.printStackTrace();  
	      }  
	    }  
	  
	    return strResult;  
	  }
	private static BiUnit<RSAPublicKey,RSAPrivateKey> getKeyPair(){
		 // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
       KeyPairGenerator keyPairGen = null;  
       try {  
           keyPairGen = KeyPairGenerator.getInstance("RSA");  
       } catch (NoSuchAlgorithmException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
    // 初始化密钥对生成器，密钥大小为96-1024位  
       keyPairGen.initialize(1024,new SecureRandom());  
       // 生成一个密钥对，保存在keyPair中  
       KeyPair keyPair = keyPairGen.generateKeyPair();  
       // 得到私钥  
       RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
       // 得到公钥  
       RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
       BiUnit<RSAPublicKey,RSAPrivateKey> result=new BiUnit<RSAPublicKey,RSAPrivateKey>();
       result.setK(publicKey);
       result.setO(privateKey);
       return result;
	}
	public synchronized byte[] encrypt(byte[] data) {
		try {
			if(isClient)
				data=encryption.encrypt(data, publicKey);
			else
				data=encryption.encrypt(data,privateKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return data;
	}
	public synchronized byte[] decrypt(byte[] data) {
		try {
			if(isClient)
				data=encryption.decrypt(data, publicKey);
			else
				data=encryption.decrypt(data,privateKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return data;
	}
	protected void washSeed() {
		currentSeed=56763432;
	}
	protected static long fakeRandom(int time,long[] seeds) {
		long last=56763432;
		for(int i=0;i<time;i++) {
			last = (last * seeds[0] + seeds[1])%Long.MAX_VALUE;
		}
	    return  (last / seeds[3]) % seeds[2];
	}
	protected long fakeRandom() {
		currentSeed = (currentSeed * seeds[0] + seeds[1])%Long.MAX_VALUE;
	    return  (currentSeed / seeds[3]) % seeds[2];
	}
}
