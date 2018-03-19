package com.carton.filesync.net;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import com.carton.filesync.common.util.Block;
import com.carton.filesync.common.util.ExtensionArrayList;

public class PTConnecter {
	private int PTListenerPort;
	private ServerSocket listener;
	private InetAddress ip;
	private boolean isServer;
	private boolean connectionReady;
	private ArrayList<PTSubSocket> connectionList=new ArrayList<PTSubSocket>();
	public PTConnecter(InetAddress ip,boolean isServer) {
		this.isServer=isServer;
		if(isServer)
			initServer();
		this.ip=ip;
		connectionReady=false;
	}
	public int getListenerPort() {
		return PTListenerPort;
	}
	public void createConnection(int amount,int port) {
		try {
			if(isServer) {
				for(int i=0;i<amount;i++) {
					Socket connection=listener.accept();
					PTSubSocket PTConnection=new PTSubSocket(connection,i);
					connectionList.add(PTConnection);
				}
			}else {
				for(int i=0;i<amount;i++) {
					Socket connection= new Socket(ip,port);
					PTSubSocket PTConnection=new PTSubSocket(connection,i);
					connectionList.add(PTConnection);
				}
			}
			connectionReady=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean OrderCheck() {
		int counter=0;
		for(int i=0;i<connectionList.size();i++) {
			int result;
			try {
				result = connectionList.get(i).PerformOderCheck(isServer, i);
				if(result==i)
					counter++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print(e);
				continue;
			}
		}
		if(counter==connectionList.size())
			return true;
		return false;
	}
	public void send(ExtensionArrayList<Block> list,int blockPerThread) {
		
	}
	private void initServer() {
		PTListenerPort=0;
		Random random=new Random();
		while(PTListenerPort==0) {
			int tempPort=random.nextInt(5000)+60000;
			try {
				listener=new ServerSocket(tempPort);
				PTListenerPort=tempPort;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				PTListenerPort=0;
			}
		}
	}
}
