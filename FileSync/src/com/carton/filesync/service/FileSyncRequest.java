package com.carton.filesync.service;

import java.io.Serializable;

public class FileSyncRequest implements Serializable{
	private final String id;
	private final String request;
	private final String serviceType;
	public static final String GETFILELIST="GFL";
	public static final String BEGINFILESYNC="BFS";
	public static final String LOGINNEWCLIENT="LNC";
	public static final String SECURITYVERIFYINFOMATION="SVI";
	public FileSyncRequest(String id,String request,String serviceType) {
		this.id=id;
		this.request=request;
		this.serviceType=serviceType;
	}
	public byte[] getUDPPacket() {
		String result=id+"$"+request+"$"+serviceType;
		return result.getBytes();
	}
	public String getId() {
		return id;
	}
	public String getRequest() {
		return request;
	}
	public String getServiceType() {
		return serviceType;
	}
}
