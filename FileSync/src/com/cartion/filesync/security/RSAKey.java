package com.cartion.filesync.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.carton.filesync.common.util.BiUnit;

public class RSAKey extends KeyUnit {
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	int mode;
	boolean seccess;
	public RSAKey(RSAPrivateKey privateKey) {
			this(null,privateKey);
	}
	public RSAKey(RSAPublicKey publicKey) {
		this(publicKey, null);
	}
	public RSAKey(RSAPublicKey publicKey,RSAPrivateKey privateKey) {
		mode=0;
		seccess=true;
		if(publicKey!=null)
			mode--;
		if(privateKey!=null)
			mode++;
		if((privateKey==publicKey)&&publicKey==null)
			seccess=false;
	}
	@Override
	public int decryptSize(int originSize) {
		// TODO Auto-generated method stub
		return ((originSize/128)+1)*128;
	}

	@Override
	public int encryptSize(int encryptionSize) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public byte[] decrypt(byte[] in) {
		// TODO Auto-generated method stub
		ByteArrayInputStream bIn=new ByteArrayInputStream(in);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[128];
		try {
			while(bIn.read(buff)!=-1){	
					bOut.write(decryptUnit(buff));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return bOut.toByteArray();
	}

	@Override
	public byte[] encrypt(byte[] in) {
		// TODO Auto-generated method stub
		int pointer=0;
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		while(true) {
			byte[] buff=new byte[117];
			for(int i=0;i<117;i++) {
				if(pointer<in.length) {
					buff[i]=in[pointer];
					pointer++;
				}else
					break;
			}
			try {
				bOut.write(encryptUnit(buff));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if(pointer>=in.length)
				break;
		}
		return bOut.toByteArray();
	}
	private byte[] encryptUnit(byte[] in) throws Exception {
		Key key=null;
		if(!this.seccess)
			return null;
		if (mode == -1)
			key=this.publicKey;
		else if(mode==1)
			key=this.privateKey;
		else
			key=this.publicKey;
		Cipher cipher = null;
		try {
			// ʹ��Ĭ��RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] output = cipher.doFinal(in);
			//System.out.println(output.length);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("�޴˼����㷨");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("����˽Կ�Ƿ�,����");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("���ĳ��ȷǷ�");
		} catch (BadPaddingException e) {
			throw new Exception("������������");
		}
	}
	private byte[] decryptUnit(byte[] in) throws Exception {
		Key key=null;
		if(!this.seccess)
			return null;
		if (mode == -1)
			key=this.publicKey;
		else if(mode==1)
			key=this.privateKey;
		else
			key=this.publicKey;
		Cipher cipher = null;
		try {
			// ʹ��Ĭ��RSA
			cipher = Cipher.getInstance("RSA");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] output = cipher.doFinal(in);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("�޴˽����㷨");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("����˽Կ�Ƿ�,����");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("���ĳ��ȷǷ�");
		} catch (BadPaddingException e) {
			throw new Exception("������������");
		}
	}
	@Override
	public String getCypherType() {
		// TODO Auto-generated method stub
		
		return "RSA";
	}
	public static BiUnit<RSAPublicKey,RSAPrivateKey> getKeyPair(){
       KeyPairGenerator keyPairGen = null;  
       try {  
           keyPairGen = KeyPairGenerator.getInstance("RSA");  
       } catch (NoSuchAlgorithmException e) {  
           // TODO Auto-generated catch block  
           e.printStackTrace();  
       }  
       keyPairGen.initialize(1024,new SecureRandom());  
       KeyPair keyPair = keyPairGen.generateKeyPair();  
       RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
       RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
       BiUnit<RSAPublicKey,RSAPrivateKey> result=new BiUnit<RSAPublicKey,RSAPrivateKey>();
       result.setK(publicKey);
       result.setO(privateKey);
       return result;
	}

}
