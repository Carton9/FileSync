package com.carton.filesync.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;

import com.cartion.filesync.security.SignProducer;

public class ServiceDiscover {
	private static final int TIMEOUT = 5000;
	private static final int MAXNUM = 3; 
	private static final int sendPort=40000;
	private static final int receviePort=40001;
	private static final byte ClientMark=(byte)'c';
	private static final byte ServerMark=(byte)'s';
	InetAddress broadcast;
	DatagramSocket ds;
	DatagramPacket dp_send;
	DatagramPacket dp_receive;
	Timer timer;
	boolean finishInit;
	SignProducer signMaker;
	boolean isServer;
	byte[] data;
	public ServiceDiscover(boolean isServer) throws SocketException, UnknownHostException {
		ds = new DatagramSocket(receviePort);
		broadcast = InetAddress.getByName("255.255.255.255");
		ds.setSoTimeout(TIMEOUT);
		this.isServer=isServer;
		timer=new Timer();
	}
	public boolean loadInfo(SignProducer signMaker) {
		finishInit=true;
		this.signMaker=signMaker;
		try {
			data=createDatagram();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private byte[] createDatagram() throws IOException {
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		if(finishInit) {
			bOut.write(InetAddress.getLocalHost().getAddress());
			Random maker=new Random();
			int port=-1;
			while(port<0) {
				port=maker.nextInt(9999)+30000;
				if(isLocalPortUsing(port))
					break;
				else 
					port=-1;
			}
			byte[] portInfo=new byte[2];
			portInfo[0]=(byte)((port >>> 8) & 0xFF);
			portInfo[1]=(byte)((port >>> 0) & 0xFF);
			bOut.write(portInfo);
			if(isServer)
				bOut.write(ServerMark);
			else
				bOut.write(ClientMark);
			bOut.write(signMaker.getSign().getO().getBytes());
			bOut.write(FileSyncNetInfo.version.getBytes());
			return bOut.toByteArray();
		}else
			return null;
	}
    public static boolean isLocalPortUsing(int port){  
        boolean flag = true;  
        try {
            flag = isPortUsing("127.0.0.1", port);  
        } catch (Exception e) {  
        }  
        return flag;  
    } 
    private static boolean isPortUsing(String host,int port) throws UnknownHostException{  
        boolean flag = false;  
        InetAddress Address = InetAddress.getByName(host);  
        try {  
            Socket socket = new Socket(Address,port);
            flag = true;  
            socket.close();
        } catch (IOException e) {  

        }  
        return flag;  
    }  
}
