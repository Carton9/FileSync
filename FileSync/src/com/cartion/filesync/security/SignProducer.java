package com.cartion.filesync.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.carton.filesync.common.util.BiUnit;

public class SignProducer {
	private String keyBase;
	private String keyPeer;
	private boolean avaliable;
	String testUnit="";
	public SignProducer(String keyBase){
		this.keyBase=keyBase;
		avaliable=false;
	}
	public SignProducer(String keyBase,String testUnit){
		this.keyBase=keyBase;
		avaliable=false;
		this.testUnit=testUnit;
	}
	public BiUnit<Boolean,Long> checkSync(long time){
		long sendTime=time;
		long systemTime=System.currentTimeMillis();
		BiUnit<Boolean,Long> result=new BiUnit<Boolean,Long>();
		if(Math.abs(systemTime-sendTime)<(30000)){
			result.setK(true);
			result.setO(Math.abs(systemTime-sendTime));
		}else{
			result.setK(false);
		}
		return result;
	}
	public BiUnit<Long,String> getSign(){
			long systemTime=System.currentTimeMillis();
			String text=(System.currentTimeMillis()/30000)+"";
			String key=SHA512(keyBase+text);
			BiUnit<Long,String> result=new BiUnit<Long,String>();
			result.setK(systemTime);
			result.setO(key);
			//keyBase=key;
			//System.out.println(testUnit+" get key "+key);
			return result;
	}
	public long getTime(){
		return System.currentTimeMillis();
	}
	public boolean isMatch(String signData){
		if(avaliable){
			String newSign=SHA512(keyPeer+(getTime()/30000)+"");
			//System.out.println("get newSign "+newSign);
			if(signData.equals(newSign)){
				//keyPeer=newSign;
				return true;
			}
		}
		return false;
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
	public void setKeyPeer(String keyPeer) {
		//this.keyPeer =SHA512(keyPeer+(getTime()/30000)+"");;
		this.keyPeer=keyPeer;
		avaliable=true;
	}
	public String getKeyPeer() {
		return SHA512(keyPeer+(getTime()/30000)+"");
		//return keyPeer;
	}
	public String getKeyPeer2() {
		//return SHA512(keyPeer+(getTime()/30000)+"");
		return keyPeer;
	}
	public boolean isAvaliable() {
		return avaliable;
	}
}
/*
long sendTime=Long.parseLong(packet.getResult());
long systemTime=System.currentTimeMillis();
DataPacket resultPacket=null;
if(Math.abs(systemTime-sendTime)<(10*1000)){
	resultPacket=new DataPacket(3,"","Finish_Sync",null,sign);
}else{
	resultPacket=new DataPacket(4,"","Error_HighPing",null,sign);
}*/