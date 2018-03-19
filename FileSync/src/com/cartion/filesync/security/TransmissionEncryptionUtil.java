package com.cartion.filesync.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class TransmissionEncryptionUtil {
	public synchronized byte[] encrypt(byte[] data,RSAPrivateKey privateKey) throws IOException, Exception{
		ByteArrayInputStream bIn=new ByteArrayInputStream(data);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[117];
		while(bIn.read(buff)!=-1){
			bOut.write(encryptUnit(buff,privateKey));
		}
		return bOut.toByteArray();
	}
	public synchronized byte[] decrypt(byte[] data,RSAPrivateKey privateKey) throws IOException, Exception{
		ByteArrayInputStream bIn=new ByteArrayInputStream(data);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[128];
		while(bIn.read(buff)!=-1){
			bOut.write(decryptUnit(buff,privateKey));
		}
		
		return bOut.toByteArray();
	}
	private byte[] encryptUnit(byte[] plainTextData,RSAPrivateKey privateKey)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("����˽ԿΪ��, ������");
		}
		Cipher cipher = null;
		try {
			// ʹ��Ĭ��RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
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
	private byte[] decryptUnit(byte[] cipherData,RSAPrivateKey privateKey)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("����˽ԿΪ��, ������");
		}
		Cipher cipher = null;
		try {
			// ʹ��Ĭ��RSA
			cipher = Cipher.getInstance("RSA");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
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
	public synchronized byte[] encrypt(byte[] data,RSAPublicKey publicKey) throws IOException, Exception{
		ByteArrayInputStream bIn=new ByteArrayInputStream(data);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[117];
		while(bIn.read(buff)!=-1){
			bOut.write(encryptUnit(buff,publicKey));
		}
		return bOut.toByteArray();
	}
	public synchronized byte[] decrypt(byte[] data,RSAPublicKey publicKey) throws IOException, Exception{
		ByteArrayInputStream bIn=new ByteArrayInputStream(data);
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		byte[] buff=new byte[128];
		while(bIn.read(buff)!=-1){
			bOut.write(decryptUnit(buff,publicKey));
		}
		return bOut.toByteArray();
	}
	private byte[] encryptUnit(byte[] plainTextData,RSAPublicKey publicKey)  
	            throws Exception {  
	        if (publicKey == null) {  
	            throw new Exception("���ܹ�ԿΪ��, ������");  
	        }  
	        Cipher cipher = null;  
	        try {  
	            // ʹ��Ĭ��RSA  
	            cipher = Cipher.getInstance("RSA");  
	            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
	            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
	            byte[] output = cipher.doFinal(plainTextData);  
	           // System.out.println(output.length);
	            return output;  
	        } catch (NoSuchAlgorithmException e) {  
	            throw new Exception("�޴˼����㷨");  
	        } catch (NoSuchPaddingException e) {  
	            e.printStackTrace();  
	            return null;  
	        } catch (InvalidKeyException e) {  
	            throw new Exception("���ܹ�Կ�Ƿ�,����");  
	        } catch (IllegalBlockSizeException e) {  
	            throw new Exception("���ĳ��ȷǷ�");  
	        } catch (BadPaddingException e) {  
	            throw new Exception("������������");  
	        }  
	   }  
	private byte[] decryptUnit(byte[] cipherData,RSAPublicKey publicKey)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("���ܹ�ԿΪ��, ������");  
        }  
        Cipher cipher = null;  
        try {  
            // ʹ��Ĭ��RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            byte[] output = cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴˽����㷨");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("���ܹ�Կ�Ƿ�,����");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("���ĳ��ȷǷ�");  
        } catch (BadPaddingException e) {  
            throw new Exception("������������");  
        }  
    }  
}
