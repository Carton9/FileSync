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

import com.cartion.filesync.security.SignProducer;
import com.carton.filesync.service.NetworkSocketManager;

public class ServiceDiscover {
	private static final int TIMEOUT = 1000;
	private static final int MAXNUM = 3; 
	private static final int sendPort=40000;
	private static final int receviePort=40001;
	private static final byte ClientMark=(byte)'c';
	private static final byte ServerMark=(byte)'s';
	private static final byte SecureMark=(byte)'S';
	private static final byte UnSecureMark=(byte)'U';
	InetAddress broadcast;
	DatagramSocket ds;
	DatagramPacket dp_send;
	DatagramPacket dp_receive;
	Timer timer;
	boolean finishInit;
	SignProducer signMaker;
	boolean isServer;
	byte[] data;
	int port;
	TimerTask sendInfo;
	TimerTask renewInfo;
	NetworkSocketManager manager;
	public class CompilableDatagram{
		InetAddress ipAddress;
		int portInfo;
		String idInfo;
		char serverMarkInfo;
		char secureMark;
		String versionInfo;
	}
	public ServiceDiscover(boolean isServer,NetworkSocketManager manager) throws SocketException, UnknownHostException {
		ds = new DatagramSocket(receviePort);
		broadcast = InetAddress.getByName("255.255.255.255");
		ds.setSoTimeout(TIMEOUT);
		this.isServer=isServer;
		timer=new Timer();
		this.manager=manager;
	}
	public boolean loadInfo(SignProducer signMaker) {
		finishInit=true;
		this.signMaker=signMaker;
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
	public void execute() {
		CompilableDatagram datagram=recevie();
		if(datagram==null)
			return;
		if(this.isServer&&datagram.serverMarkInfo==this.ClientMark) {
			serverProcess(datagram);
		}
	}
	private void serverProcess(CompilableDatagram datagram) {
		
		try {
			data=createDiscoverDatagram();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private CompilableDatagram recevie() throws IOException {
		byte[] buff=new byte[140];
		dp_receive = new DatagramPacket(buff, 140);
		try {
			ds.receive(dp_receive);
		}catch(InterruptedIOException e){
			return null;
		}
		buff=dp_receive.getData();
		ByteArrayInputStream bis=new ByteArrayInputStream(buff);
		byte ip[]=new byte[4];
		byte port[]=new byte[2];
		char serverMark=0;
		byte id[]=new byte[128];
		char secureMark=0;
		byte version[]=new byte[4];
		bis.read(ip);
		bis.read(port);
		serverMark=(char)bis.read();
		bis.read(id);
		secureMark=(char)bis.read();
		bis.read(version);
		InetAddress ipAddress=InetAddress.getByAddress(ip);
		int portInfo=(port[0] << 8) + (port[1] << 0);
		String idInfo=new String(id);
		String versionInfo=new String(version);
		CompilableDatagram datagram=new CompilableDatagram();
		datagram.ipAddress=ipAddress;
		datagram.idInfo=idInfo;
		datagram.portInfo=portInfo;
		datagram.serverMarkInfo=serverMark;
		datagram.versionInfo=versionInfo;
		return datagram;
	}
	private byte[] createDiscoverDatagram() throws IOException {
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		if(finishInit) {
			bOut.write(InetAddress.getLocalHost().getAddress());
			Random maker=new Random();
			port=-1;
			while(port<0) {
				port=maker.nextInt(9999)+30000;
				if(isLocalPortUsing(port)&&!manager.checkPort(port))
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
			bOut.write(this.SecureMark);
			bOut.write(FileSyncNetInfo.version.getBytes());
			return bOut.toByteArray();
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
