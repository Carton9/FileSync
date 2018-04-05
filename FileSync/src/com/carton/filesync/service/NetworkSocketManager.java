package com.carton.filesync.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.carton.filesync.file.FileIO;
import com.carton.filesync.net.ControlSocket;
import com.carton.filesync.net.DuplexControlSocket;
import com.carton.filesync.net.NetworkVerifier;
import com.carton.filesync.net.SecurityControlSocket;
import com.carton.filesync.net.ServiceDiscover;
import com.carton.filesync.net.TCPFrame;

public class NetworkSocketManager {
	ConcurrentHashMap<String,DuplexControlSocket> controlSocketMap;
	ConcurrentHashMap<String,NetworkMachineInfomation> infoList=new ConcurrentHashMap<String,NetworkMachineInfomation>();
	ServiceDiscover discover;
	public class NetworkMachineInfomation {
		InetAddress ip;
		int port;
		String id;
		boolean isSecure;
		boolean isConnected;
		public NetworkMachineInfomation(InetAddress ip,
		int port,
		String id,
		boolean isSecure) {
			this.port=port;
			this.ip=ip;
			this.id=id;
			this.isSecure=isSecure;
			
		}
	}
	boolean usedList[]=new boolean[65536];
	public NetworkSocketManager() {
		for(int i=0;i<65536;i++)
			usedList[i]=false;
		NetworkVerifier v=new NetworkVerifier() {

			@Override
			public boolean veriftyID(String id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean veriftyPort(int port) {
				// TODO Auto-generated method stub
				return checkPort(port);
			}
			
		};
		try {
			this.discover=new ServiceDiscover(false,v);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addInfo(NetworkMachineInfomation info) {
		this.registeredPort(info.port);
		infoList.put(info.id, info);
		
	}
	public boolean inquireMachine(String id) {
		if(infoList.contains(id)) {
			if(!infoList.get(id).isConnected)
				return true;
		}
		return false;
	}
	public boolean checkPort(int port) {//return true mean used
		return usedList[port];
	}
	public synchronized boolean registeredPort(int port){
		if(checkPort(port))
			return false;
		else {
			usedList[port]=true;
			return true;
		}
	}
	public boolean addControlSocket(String id,DuplexControlSocket socket) {
		controlSocketMap.put(id, socket);
		return true;
	}
	public String[] keys() {
		return controlSocketMap.keySet().toArray(new String[controlSocketMap.size()]);
	}
	public DuplexControlSocket removeControlSocket(String id) {
		return controlSocketMap.remove(id);
	}
	public DuplexControlSocket getSocket(String id) {
		return controlSocketMap.get(id);
	}
	public void submitFrameThough(String id,TCPFrame frame) throws IOException {
		controlSocketMap.get(id).submitFrame(frame);
	}
	public void submitFrameThough(String id,List<TCPFrame> frames) throws IOException {
		controlSocketMap.get(id).submitFrame(frames);
	}
	public void closeControlSocket(String id) throws Exception {
		controlSocketMap.get(id).close();
		controlSocketMap.remove(id);
	}
	public void closeAllControlSocket() {
		String keys[]=keys();
		for(String i:keys) {
			try {
				closeControlSocket(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
