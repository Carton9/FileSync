package com.carton.filesync.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceDiscover {
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
	private void recevie() throws IOException {
		int tv=0;
		try {
			ds.receive(dp_receive);
			byte[] data=dp_receive.getData();tv++;
			String dataInfo=new String(data);
			String[] infos=dataInfo.split("$");tv++;
			if(infos.length<2)
				return;
			String id=infos[1];
			InetAddress ip=dp_receive.getAddress();
			int port=Integer.parseInt(infos[0]);
			if(this.log.veriftyID(id)) {
				this.log.logPort(port);
				createDatagram();
				
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
