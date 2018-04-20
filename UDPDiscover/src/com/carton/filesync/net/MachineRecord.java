package com.carton.filesync.net;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MachineRecord {
	private InetAddress ip;
	private int port;
	private String id;
	private long time;
	private static ConcurrentHashMap<String,MachineRecord> machineMap=new ConcurrentHashMap<String,MachineRecord>();
	public static boolean logMachine(MachineRecord record) {
		System.out.println(record.getId()+" "+record.getIp());
		if(machineMap.containsKey(record.id))return false;
		else machineMap.put(record.id, record);
		return true;
	}
	public static boolean logMachine(InetAddress ip,int port,String id,long time) {
		return logMachine(new MachineRecord(ip,port,id,time));
	}
	public static MachineRecord getRecord(String id) {return machineMap.get(id);}
	public static boolean isLogged(String id) {return machineMap.containsKey(id);}
	public MachineRecord(InetAddress ip,int port,String id,long time) {
		this.ip=ip;
		this.port=port;
		this.id=id;
	}
	public static boolean removeMachine(String id) {
		return machineMap.remove(id) != null;
	}
	
	/**
	 * @return the IP address
	 */
	public InetAddress getIp() {
		return ip;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
}
