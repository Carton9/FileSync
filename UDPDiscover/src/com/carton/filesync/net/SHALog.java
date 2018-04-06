package com.carton.filesync.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHALog extends SecurityLog {
	public SHALog() {
		this.id=SHA512("1111111");
		connecterID=new String[1];
		this.connecterCount=this.connecterID.length;
	}
	@Override
	public boolean veriftyID(String id) {
		// TODO Auto-generated method stub
		for(int i=0;i<connecterCount;i++) {
			String currentID=SHA512(connecterID[i]+(System.currentTimeMillis()/this.TIMEOUT));
			if(currentID.equals(id))
				return true;
		}
		return false;
	}
	@Override
	public boolean isConnecter(String connecter) {
		// TODO Auto-generated method stub
		return veriftyID(id);
	}

	@Override
	public String generateSign() {
		return SHA512(id+(System.currentTimeMillis()/this.TIMEOUT));
	}
	private String SHA512(String strText)  
	  {  
	    return SHA(strText, "SHA-512");  
	  }  
	private String SHA(final String strText, final String strType)  
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
	@Override
	public String addNewConnecter() {
		// TODO Auto-generated method stub
		return null;
	}
}
