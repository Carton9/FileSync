package com.carton.filesync.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.carton.filesync.common.util.GeneralService;

public class ServiceDiscover implements GeneralService {
	private static int runningCount=0;
	private static final int TIMEOUT = 5000;
	int sendPort=29999;
	int receviePort=29998;
	InetAddress locoal;
	DatagramSocket ds;
	DatagramPacket dp_send;
	DatagramPacket dp_receive;
	Timer timer;
	boolean isServer;
	byte[] data;
	int port;
	TimerTask sendInfo;
	SecurityLog log;
	boolean isAlive=false;
	String divider="@";
	String endPoint="#";
	NetworkManager manager;
	public ServiceDiscover(boolean isServer) {
		runningCount++;
		this.isServer=isServer;
		this.log=new SHALog();
		byte[] buf=new byte[2048];
		this.dp_receive=new DatagramPacket(buf, 2048);
	}
	public ServiceDiscover(boolean isServer,SecurityLog log,NetworkManager manager) {
		runningCount++;
		this.isServer=isServer;
		this.log=log;
		byte[] buf=new byte[2048];
		this.manager=manager;
		this.dp_receive=new DatagramPacket(buf, 2048);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Service Discover "+runningCount;
	}
	
	@Override
	public void initialize() {
		try {
			if(isServer)
				ds = new DatagramSocket(receviePort);
			else
				ds = new DatagramSocket(sendPort);
			ds.setSoTimeout(TIMEOUT);
			createDatagram();
			this.stateFlags[0]=true;
			isAlive=true;
			System.out.println("initialize "+getName());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	}
	@Override
	public void execute() {
		Thread receiveThread=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					try {
						recevie();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread boardcastThread=null;
		if(isServer){
			boardcastThread=new Thread() {
				public void run() {
					while(true) {
						try {
							boardcast();
							Thread.sleep(1000);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}
				}
			};
			boardcastThread.start();
		}
			
		receiveThread.start();
		while(isAlive);
	}
	@Override
	public void finish() {
	}
	private void createDatagram() {
		this.port=-1;
		for(int i=30000;i<40000;i++) {
			if(log.veriftyPort(i)){
				port=i;
				break;
			}
		}
		if(this.port==-1)
			return;
		String dataString=log.generateSign()+divider+port;
		if(data!=null) {
			synchronized(data) {
				data=dataString.getBytes();
			}
		}else {
			data=dataString.getBytes();
		}
		
	}
	private byte[] getData() {
		synchronized(data) {
			byte[] copyData=new byte[data.length];
			System.arraycopy(data, 0, copyData, 0, data.length);
			return copyData;
		}
	}
	private void recevie() throws IOException, InterruptedException{
		while(isAlive) {
			byte[] buf=new byte[2048];
			this.dp_receive=new DatagramPacket(buf, 2048);
			if(isServer){
				System.out.println("server");
				recevieByServer();
			}
			else
				recevieByClient();
		}
		
	}
	private void recevieByClient() throws IOException {
		int tv=0;
		try {
			//if(ds==null)
			//	ds=new DatagramSocket(receviePort);
			ds.receive(dp_receive);
			byte[] data=dp_receive.getData();tv++;
			String dataInfo=new String(data);
			String[] infos=dataInfo.split(divider);tv++;
			if(infos.length<2)
				return;
			String id=infos[0];
			InetAddress ip=dp_receive.getAddress();
			int port=Integer.parseInt(infos[1].replace(" ", "").trim());
			////////////////////////////////////////////////////////
			if(this.log.veriftyID(id)&&this.log.logPort(port)) {
				if(manager.isLogged(id))
					return;
				manager.logMachine(ip, port, id.trim(), this.log.getTime());tv++;
				//TODO reply 
				String sendData=log.generateSign()+divider+port;
				byte[] buf=new byte[2048];
				DatagramPacket tempR=new DatagramPacket(buf, 2048);
				DatagramSocket tempDS = new DatagramSocket(port);
				System.out.println(tempDS.getLocalPort());
				tempDS.send(new DatagramPacket(sendData.getBytes(),sendData.length(),ip,this.receviePort));
				tempDS.receive(tempR);
				String recevie=new String(tempR.getData());
				if(recevie.contains(endPoint)) {
					if(!recevie.split(endPoint)[0].equals(log.generateSign()+divider)){
						manager.removeMachine(id);
						log.freePort(port);
					}
					tempDS.close();
				}
			}
		}catch(SocketTimeoutException c){
			System.out.println("time out client");
		}catch(InterruptedIOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(this.getClass()+" recevieByClient() "+tv+" "+dp_receive.getLength());
	}
	private void recevieByServer() throws IOException {
		int tv=0;
		try {
			ds.receive(dp_receive);
			byte[] data=dp_receive.getData();tv++;
		
			String dataInfo=new String(data);
			System.out.println(dataInfo);
			String[] infos=dataInfo.split(divider);tv++;
			if(infos.length<2)
				return;
			String id=infos[0];
			InetAddress ip=dp_receive.getAddress();
			int port=Integer.parseInt(infos[1].trim());
			if(this.log.veriftyID(id)) {
				this.log.logPort(port);
				manager.logMachine(ip, port, id, this.log.getTime());tv++;
				createDatagram();
				DatagramSocket tempDS = new DatagramSocket(port);
				String sendData=log.generateSign()+divider+endPoint;
				tempDS.send(new DatagramPacket(sendData.getBytes(),sendData.length(),ip,port));
				tempDS.close();
			}
		}catch(SocketTimeoutException e){
			System.out.println("time out server");
		}catch(InterruptedIOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(this.getClass()+" recevieByServer() "+tv);
	}
	private void boardcast() throws UnknownHostException,IOException {
		System.out.println("bc");
		byte[] sendData=getData();
		dp_send= new DatagramPacket(sendData,sendData.length,InetAddress.getByName("255.255.255.255"),sendPort);
		ds.send(dp_send);
	}
	
}
