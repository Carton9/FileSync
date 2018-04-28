package com.carton.filesync.net;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.cartion.filesync.security.KeyUnit;
import com.carton.filesync.common.util.GeneralManager;

public class NetworkManager extends GeneralManager {
	protected HashMap<String,MachineRecord> machineMap;
	protected HashMap<String,DuplexControlSocket> connectionMap;
	private int MaxControlSocket=-1;
	private int ScannSpeed=-1;
	private ArrayList<String> bandIP=new ArrayList<String>();
	public NetworkManager() {
		machineMap=new HashMap<String,MachineRecord>();
		connectionMap=new HashMap<String,DuplexControlSocket>();
	}
	public NetworkManager(int ScannSpeed,int MaxControlSocket) {
		this();
		this.ScannSpeed=ScannSpeed;
		this.MaxControlSocket=MaxControlSocket;
		
	}
	public boolean logMachine(MachineRecord record) {
		
		//System.out.println(machineMap.size());
		synchronized(machineMap) {
			//System.out.println(machineMap.size());
			for(String i:machineMap.keySet()) {
				if(i.equals(record.getId()))
					return false;
			}
			if(bandIP.contains(record.getIp().getHostAddress()))
				return false;
			machineMap.put(record.getId(), record);
		}
		return true;
	}
	public boolean logMachine(InetAddress ip,int port,String id,long time) {
		return logMachine(new MachineRecord(ip,port,id,time));
	}
	public MachineRecord getRecord(String id) {
		synchronized(machineMap) {return machineMap.get(id);}
		}
	public boolean isLogged(String id) {
		//System.out.println("size "+machineMap.size());
		return machineMap.containsKey(id);
	}
	public boolean removeMachine(String id) {
		return machineMap.remove(id) != null;
	}
	
	public DuplexControlSocket getTCPConnection(String id,KeyUnit key) throws IOException, InterruptedException {
		synchronized(machineMap) {
			if(connectionMap.size()>this.MaxControlSocket)
				return null;
			MachineRecord record=machineMap.get(id);
			DuplexControlSocket dcs=new DuplexControlSocket(record.getIp(),record.getPort(),key);
			connectionMap.put(id, dcs);
			return dcs;
		}
	}
	public void closeTCPConnection(String id) throws Exception {
		synchronized(machineMap) {
			connectionMap.get(id).close();
		}
	}
	
	public void addBandIP(InetAddress ip) {
		this.addBandIP(ip.getHostAddress());
	}
	public void addBandIP(String ip) {
		bandIP.add(ip);
	}
	@Override
	public void loadConfiguration(byte[] data) {
		// TODO Auto-generated method stub
		String info=new String(data);
		String infos[]=info.split("\n");
		for(int i=0;i<infos.length;i++) {
			String item[]=infos[i].split(": ");
			if(item[0].equals("MaxControlSocket")) 
				MaxControlSocket=Integer.parseInt(item[1].replaceAll(" ", "").trim());
			if(item[0].equals("Bad IP"))
				bandIP.add(item[1]);
		}
	}
	@Override
	public byte[] saveConfiguration() {
		// TODO Auto-generated method stub
		StringBuffer builder=new StringBuffer();
		builder.append("MaxControlSocket: "+MaxControlSocket+"\n");
		for(int i=0;i<bandIP.size();i++) {
			builder.append("Bad IP: "+bandIP.get(i)+"\n");
		}
		return builder.toString().getBytes();
	}
}
