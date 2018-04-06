package com.carton.filesync.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.carton.filesync.service.NetworkSocketManager;
import com.carton.filesync.service.NetworkSocketManager.NetworkMachineInfomation;

public class ServiceDiscover {
	private static final int TIMEOUT = 1000;
	private static final int MAXNUM = 3; 
	private static final int sendPort=40000;
	private static final int receviePort=40001;
	public static final byte ClientMark=(byte)'c';
	public static final byte ServerMark=(byte)'s';
	public static final byte SecureMark=(byte)'S';
	public static final byte UnSecureMark=(byte)'U';
	InetAddress broadcast;
	DatagramSocket ds;
	DatagramPacket dp_send;
	DatagramPacket dp_receive;
	Timer timer;
	boolean finishInit;
	boolean isServer;
	byte[] data;
	int port;
	TimerTask sendInfo;
	TimerTask renewInfo;
	NetworkVerifier verifier;
	
	public ServiceDiscover(boolean isServer,NetworkVerifier verifier) throws SocketException, UnknownHostException {
		ds = new DatagramSocket(receviePort);
		broadcast = InetAddress.getByName("255.255.255.255");
		ds.setSoTimeout(TIMEOUT);
		this.isServer=isServer;
		timer=new Timer();
		this.verifier=verifier;
	}
	public boolean loadInfo() {
		finishInit=true;
		//.signMaker=signMaker;
		try {
			data=createDiscoverDatagram();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void init() throws SocketException {
		ds.setSoTimeout(TIMEOUT);
		sendInfo=new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized(data) {
					if(data==null)
						return;
					dp_send=new DatagramPacket(data,data.length,broadcast,sendPort);
				}
				synchronized(ds) {
					try {
						ds.send(dp_send);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}};
		renewInfo=new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					synchronized(data) {
						try {
							data=createDiscoverDatagram();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			};
		timer.schedule(sendInfo,1000);
		timer.schedule(renewInfo,30*60*1000);
	}
	public CompilableDatagram execute() {
		CompilableDatagram datagram;
		try {
			datagram = recevie();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(datagram==null)
			return null;
		if(this.isServer&&datagram.serverMarkInfo==this.ClientMark) {
			if(verifier.veriftyID(datagram.idInfo))
				return datagram;
			return null;
		}
		else if(!this.isServer&&datagram.serverMarkInfo==this.ServerMark){
			if(verifier.veriftyID(datagram.idInfo))
				return datagram;
			return null;
		}
		return null;
	}
	private void serverProcess(CompilableDatagram datagram) {
	}
	private CompilableDatagram recevie() throws IOException {
		byte[] buff=new byte[CompilableDatagram.getDataLength()];
		dp_receive = new DatagramPacket(buff, CompilableDatagram.getDataLength());
		try {
			ds.receive(dp_receive);
		}catch(InterruptedIOException e){
			return null;
		}
		buff=dp_receive.getData();
		CompilableDatagram datagram=CompilableDatagram.decompileData(buff);
		return datagram;
	}
	private byte[] createDiscoverDatagram() throws IOException {
		if(finishInit&&isServer) {
			Random maker=new Random();
			port=-1;
			while(port<0) {
				port=maker.nextInt(9999)+30000;
				if(isLocalPortUsing(port)&&!verifier.veriftyPort(port))
					break;
				else 
					port=-1;
			}
			CompilableDatagram datagram=new CompilableDatagram();
			datagram.serverMarkInfo=ServerMark;
			datagram.ipAddress=InetAddress.getLocalHost();
			datagram.idInfo="";
			datagram.portInfo=port;
			datagram.secureMark=(char) this.SecureMark;
			datagram.versionInfo=FileSyncNetInfo.version;
			return datagram.compileInformation();
		}else
			return null;
	}
	private static boolean isLocalPortUsing(int port){  
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
