package com.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;

public class ControlSocket {
	private final static String CONTROLCONNECT="CSCO";
	private final static String DATACONNECT="DSCO";
	private final static String ACCEPT="ACPT";
	private final static String DISCONNECT="DSCO";
	
	private final static String SYNCSIGN="SYSI";
	private final static String CONFIRM="CONF";
	
	public final static String PACKETFRAME="PKFM";
	public final static String STREAMFRAME="SMFM";
	public final static String PTSTREAMFRAME="PTFM";
	
	private final static String DATAPIPESYNC="DPSC";
	private final static int commendLength=4;
	private final static int MaxDataPipePerFrame=1000;
	private Socket controlPipe;
	private ServerSocket controlListenerPipe;
	public HashMap<String,Socket> dataSocketMap;
	public HashMap<Socket,ObjectLock> LockMap;
	private InetAddress ip;private int port;
	DataInputStream cis;
	DataOutputStream cos;
	ExecutorService pool;
	public ControlSocket(InetAddress ip,int port) throws IOException {
		controlPipe=new Socket(ip,port);
		controlPipe.getOutputStream().write(CONTROLCONNECT.getBytes());
		byte[] recevie=new byte[commendLength];
		controlPipe.getInputStream().read(recevie);
		if((new String(recevie)).equals(ACCEPT)) {
			this.ip=ip;
			this.port=port;
		}else {
			System.out.println(new String(recevie));
			throw new IOException("no reply");
		}
		dataSocketMap=new HashMap<String,Socket>();
		LockMap=new HashMap<Socket,ObjectLock>();
		cis=new DataInputStream(controlPipe.getInputStream());
		cos=new DataOutputStream(controlPipe.getOutputStream());
		pool=Executors.newFixedThreadPool(2000);
	}
	public ControlSocket(int port) throws IOException {
		controlListenerPipe=new ServerSocket(port);
		Socket control=controlListenerPipe.accept();
		byte[] recevie=new byte[commendLength];
		control.getInputStream().read(recevie);
		if((new String(recevie)).equals(CONTROLCONNECT)) {
			control.getOutputStream().write(ACCEPT.getBytes());
			this.controlPipe=control;
			this.ip=InetAddress.getLocalHost();
			this.port=port;
		}else {
			System.out.println(new String(recevie));
			throw new IOException("no reply");
		}
		dataSocketMap=new HashMap<String,Socket>();
		LockMap=new HashMap<Socket,ObjectLock>();
		cis=new DataInputStream(controlPipe.getInputStream());
		cos=new DataOutputStream(controlPipe.getOutputStream());
		pool=Executors.newFixedThreadPool(2000);
	}
	public ControlSocket(String ip,int port) throws IOException {
		this(InetAddress.getByName(ip),port);
	}
	public void listenPort() throws IOException {
		Socket socket=controlListenerPipe.accept();
		byte[] recevie=new byte[commendLength];
		socket.getInputStream().read(recevie);
		String commend=(new String(recevie));
		if(commend.equals(DATACONNECT)) {
			socket.getOutputStream().write(ACCEPT.getBytes());
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
			socket.getOutputStream().write(SYNCSIGN.getBytes());
			String sign=SHA512(socket.getInetAddress().getHostAddress()+(new Random()).nextLong());
			dos.writeInt(sign.length());
			socket.getOutputStream().write(sign.getBytes());
			recevie=new byte[commendLength];
			socket.getInputStream().read(recevie);
			if((new String(recevie)).equals(CONFIRM)) {
				dataSocketMap.put(sign, socket);
			}
		}
	}
	public void listenControlPipe() throws IOException {
		String commend=recevieCommend();
		TCPFrame frame=null;
		frame=TCPFrame.createFrame(commend);
		writeCommend(ACCEPT);
		commend=recevieCommend();
		if(commend.equals(DATAPIPESYNC)) {
			ArrayList<String> keyset=getDataSync();
			frame.init(keyset.toArray(new String[keyset.size()]), this);
		}
		if(frame!=null) {
			loadRunnableFrame(frame);
		}
	}
	public String requireDataSocket() throws IOException {
		Socket dataSocket=new Socket(ip,port); 
		dataSocket.getOutputStream().write(DATACONNECT.getBytes());
		DataInputStream dis=new DataInputStream(dataSocket.getInputStream());
		DataOutputStream dos=new DataOutputStream(dataSocket.getOutputStream());
		byte[] recevie=new byte[commendLength];
		dataSocket.getInputStream().read(recevie);
		if((new String(recevie)).equals(ACCEPT)) {
			recevie=new byte[commendLength];
			dataSocket.getInputStream().read(recevie);
			if((new String(recevie)).equals(SYNCSIGN)) {
				int dataLength=dis.readInt();
				byte buff[]=new byte[dataLength];
				dataSocket.getInputStream().read(buff);
				dataSocket.getOutputStream().write(CONFIRM.getBytes());
				dataSocketMap.put(new String(buff), dataSocket);
				LockMap.put(dataSocket, new ObjectLock());
				return new String(buff);
			}
		}
		return "";
	}
	public HashMap<String,BiUnit<InputStream,OutputStream>> loadPipes(String[] keys) throws IOException{
		HashMap<String,BiUnit<InputStream,OutputStream>> result=new HashMap<String,BiUnit<InputStream,OutputStream>>();
		for(int i=0;i<keys.length;i++) {
			BiUnit<InputStream,OutputStream> unit=new BiUnit<InputStream,OutputStream>();
			unit.setK(dataSocketMap.get(keys[i]).getInputStream());
			unit.setO(dataSocketMap.get(keys[i]).getOutputStream());
			result.put(keys[i], unit);
		}
		return result;
	}
	public boolean submitFrame(TCPFrame frame) throws IOException {
		String dataSocket[]=getMutilDataSocket(frame.getRequirePipeSize());
		writeCommend(frame.getFrameType());
		if(!recevieCommend().equals(ACCEPT))
			return false;
		writeCommend(DATAPIPESYNC);
		this.writeInt(this.controlPipe.getOutputStream(), dataSocket.length);
		for(int i=0;i<dataSocket.length;i++) {
			this.writeInt(this.controlPipe.getOutputStream(), dataSocket[i].length());
			controlPipe.getOutputStream().write(dataSocket[i].getBytes());
		}
		frame.init(dataSocket, this);
		return loadRunnableFrame(frame);
	}
	public boolean submitFrame(List<TCPFrame> frames) throws IOException {
		for(TCPFrame i:frames) {
			if(!submitFrame(i))
				return false;
		}
		return true;
	}
	public boolean loadRunnableFrame(TCPFrame frame) throws IOException {
		
		boolean success=frame.successInit;
		if(success) {
			
			pool.execute(new Runnable() {
				@Override
				public void run() {
					frame.execute();
					
				}
			});
			return true;
		}
		return false;
	}
	public boolean loadRunnableFrames(List<TCPFrame> frames) throws IOException {
		for(TCPFrame i:frames) {
			if(!loadRunnableFrame(i))
				return false;
		}
		return true;
	}
	public String[] getMutilDataSocket(int size) throws IOException {
		ArrayList<String> dataSocketList=new ArrayList<String>();
		for(int i=0;i<size;i++) {
			String result=requireDataSocket();
			if(!result.equals(""))
				dataSocketList.add(result);
		}
		return dataSocketList.toArray(new String[dataSocketList.size()]);
	}
	private ArrayList<String> getDataSync() throws IOException{
		int totalCount=readInt(this.controlPipe.getInputStream());
		ArrayList<String> linkDataPipe=new ArrayList<String>();
		if(MaxDataPipePerFrame<totalCount) {
			return null;
		}else {
			
			for(int i=0;i<totalCount;i++) {
				int dataLength=readInt(cis);
				byte buff[]=new byte[512];
				controlPipe.getInputStream().read(buff);
				String key=(new String(buff)).trim();
				for(String c:dataSocketMap.keySet()) {
					if(c.equals(key)) {
						linkDataPipe.add(key);
						
					}
					
				}
				
			}
			return linkDataPipe;
		}
	}
	public void closeSendingDataPipe(String key){
		closeSendingDataPipe(key,true);
	}
	public void closeSendingDataPipe(String key,boolean confirm){
		Socket dataPipe=dataSocketMap.remove(key);
		LockMap.remove(dataPipe);
		try {
			if(confirm) {
				dataPipe.getOutputStream().write(DISCONNECT.getBytes());
			}
			dataPipe.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e);
		}
		
	}
	public void closeSendingDataPipe(String key[],boolean confirm){
		for(String i:key) {
			closeSendingDataPipe(i,confirm);
		}
	}
	public void closeReceiveDataPipe(String key[],boolean confirm) {
		for(String i:key) {
			closeReceiveDataPipe(i,confirm);
		}
	}
	public void closeSendingDataPipe(String key[]){
		for(String i:key) {
			closeSendingDataPipe(i);
		}
	}
	public void closeReceiveDataPipe(String key[]) {
		for(String i:key) {
			closeReceiveDataPipe(i);
		}
	}
	public void closeReceiveDataPipe(String key) {
		closeReceiveDataPipe(key,true);
	}
	public void closeReceiveDataPipe(String key,boolean confirm) {
		Socket dataPipe=dataSocketMap.get(key);
		if(confirm) {
			byte[] recevie=new byte[commendLength];
			try {
				dataPipe.getInputStream().read(recevie);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
			String command=new String(recevie);
			if(command.equals(DISCONNECT)) {
				dataSocketMap.remove(key);
				LockMap.remove(dataPipe);
				try {
					dataPipe.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e);
				}
			}
		}else {
			dataSocketMap.remove(key);
			LockMap.remove(dataPipe);
		}
		
	}
	private void writeCommend(String commend) throws IOException {
		if(commend.length()>commendLength)
			return;
		controlPipe.getOutputStream().write(commend.getBytes());
	}
	private String recevieCommend() throws IOException {
		byte[] recevie=new byte[commendLength];
		controlPipe.getInputStream().read(recevie);
		return new String(recevie);
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
	public final int readInt(InputStream in) throws IOException {
		int length=in.read();
		System.out.println("readInt "+length);
		byte data[]=new byte[length];
		in.read(data);
		return Integer.parseInt(new String(data));
    }
	public final void writeInt(OutputStream out,int v) throws IOException {
		String data=v+"";
		out.write((byte)data.length());
		out.write(data.getBytes());
    }
}
