package com.carton.filesync.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

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
	public ServiceDiscover(boolean isServer) {
		runningCount++;
		this.isServer=isServer;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Service Discover "+runningCount;
	}
	
	@Override
	public void initialize() {
		try {
			ds = new DatagramSocket(receviePort);
			ds.setSoTimeout(TIMEOUT);
			createDatagram();
			this.stateFlags[0]=true;
			isAlive=true;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void execute() {
		Thread receiveThread=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					recevie();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		if(isServer)
			sendInfo=new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					boardcast();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		};
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
		String dataString=log.generateSign()+"$"+port;
		synchronized(data) {
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
	private void recevie() throws IOException{
		if(isServer)
			recevieByServer();
		else
			recevieByClient();
	}
	private void recevieByClient() throws IOException {
		int tv=0;
		try {
			ds.receive(dp_receive);
			byte[] data=dp_receive.getData();tv++;
			String dataInfo=new String(data);
			String[] infos=dataInfo.split("$");tv++;
			if(infos.length<2)
				return;
			String id=infos[0];
			InetAddress ip=dp_receive.getAddress();
			int port=Integer.parseInt(infos[1]);
			if(this.log.veriftyID(id)&&this.log.logPort(port)) {
				MachineRecord.logMachine(ip, port, id, this.log.getTime());tv++;
				//TODO reply 
				String sendData=id+"$-1";
				DatagramSocket tempDS = new DatagramSocket(port);
				tempDS.send(new DatagramPacket(sendData.getBytes(),sendData.length(),ip,port));
				tempDS.receive(dp_receive);
				if(!new String(dp_receive.getData()).equals(log.generateSign()+"$-1")){
					MachineRecord.removeMachine(id);
					log.freePort(port);
				}
				tempDS.close();
			}
		}catch(InterruptedIOException e){
			
		}catch(Exception e){
			
		}
		System.out.println(this.getClass()+" recevie() "+tv);
	}
	private void recevieByServer() throws IOException {
		int tv=0;
		try {
			ds.receive(dp_receive);
			byte[] data=dp_receive.getData();tv++;
			String dataInfo=new String(data);
			String[] infos=dataInfo.split("$");tv++;
			if(infos.length<2)
				return;
			String id=infos[0];
			InetAddress ip=dp_receive.getAddress();
			int port=Integer.parseInt(infos[1]);
			if(this.log.veriftyID(id)) {
				this.log.logPort(port);
				MachineRecord.logMachine(ip, port, id, this.log.getTime());tv++;
				createDatagram();
				DatagramSocket tempDS = new DatagramSocket(port);
				tempDS.receive(dp_receive);
				if(!new String(dp_receive.getData()).equals(log.generateSign()+"$-1")){
					MachineRecord.removeMachine(id);
					log.freePort(port);
				}else {
					String sendData=id+"$-1";
					tempDS.send(new DatagramPacket(sendData.getBytes(),sendData.length(),ip,port));
					
				}
				tempDS.close();
			}
		}catch(InterruptedIOException e){
			
		}catch(Exception e){
			
		}
		System.out.println(this.getClass()+" recevie() "+tv);
	}
	private void boardcast() throws UnknownHostException,IOException {
		byte[] sendData=getData();
		dp_send= new DatagramPacket(sendData,sendData.length,InetAddress.getByName("255.255.255.255"),sendPort);
		synchronized(dp_send) {
			synchronized(ds) {
				ds.send(dp_send);
			}
		}
	}
	
}
