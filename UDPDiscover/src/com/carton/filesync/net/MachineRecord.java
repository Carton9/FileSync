package com.carton.filesync.net;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MachineRecord {
	private InetAddress ip;
	private int port;
	private String id;
	private long time;
	public MachineRecord(InetAddress ip,int port,String id,long time) {
		this.ip=ip;
		this.port=port;
		this.id=id;
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
