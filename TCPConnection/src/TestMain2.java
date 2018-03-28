import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.net.DECKey;
import com.net.PTFrame;
import com.net.ResultQueue;
import com.net.StreamFrame;

public class TestMain2 {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		/*ResultQueue a=new ResultQueue();
		Thread test=new Thread() {
			public void run() {
				ArrayList result=a.getGroupedFrame(new Class[] {StreamFrame.class,StreamFrame.class,StreamFrame.class,StreamFrame.class});
				System.out.print(result);
			}
		};
		test.start();*/
		//
		//double time=10000000;
		//DECKey key=DECKey.getdefultKey();
		//System.out.println(key.decryptSize(SHA512(System.currentTimeMillis()+"").getBytes().length));
		System.out.println((30000 >>> 8) & 0xFF);
		System.out.println((30000 >>> 0) & 0xFF);
		System.out.println((117 << 8) + (48 << 0));
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
}
