package com.carton.filesync.net;

import java.net.InetAddress;

public class MachineRecord {
	private InetAddress ip;
	private int port;
	private String id;
	public MachineRecord(InetAddress ip,int port,String id) {
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
}
