package com.cartion.filesync.security;

import java.io.Serializable;

import com.carton.filesync.net.NetworkVerifier;

public abstract class SecurityLog implements NetworkVerifier,Serializable{
	protected static final int TIMEOUT=10*60*1000;//10 min
	protected String id;
	protected String parent;
	protected String connecterID[];
	protected int connecterCount;
	protected int portsLog[]=new int[65536];//0 not used,1 logged,-1 using
	public SecurityLog() {
		this("","");
	}
	public SecurityLog(String sign) {
		this(sign,"");
	}
	public SecurityLog(String sign,String parent) {
		System.out.println(sign+" "+parent);
		if(!sign.equals(""))
			this.id=sign;
		if(!parent.equals(""))
			this.parent=parent;
		connecterID=new String[1];
		if(!parent.equals(""))
			connecterID[0]=parent;
		this.connecterCount=this.connecterID.length;
		
	}
	public SecurityLog(SecurityLog parentLog) {
		this(parentLog.addNewConnecter(),parentLog.id);
	}
	public abstract boolean isConnecter(String connecter);
	public synchronized boolean veriftyPort(int port) {
		synchronized(portsLog) {
			if(portsLog[port]==0)
				return true;
			return false;
		}
	}
	public synchronized boolean logPort(int port) {
		synchronized(portsLog) {
			if(veriftyPort(port))
				portsLog[port]=1;
			else
				return false;
			return true;
		}
	}
	public synchronized boolean logUsingPort(int port) {
		synchronized(portsLog) {
			if(portsLog[port]==1) {
				portsLog[port]=-1;
				return true;
			}
			return false;	
		}
	}
	public synchronized void freePort(int port) {
		synchronized(portsLog) {
			if(portsLog[port]!=0) {
				portsLog[port]=0;
			}
		}
	}
	public abstract String generateSign();
	public abstract String generateSign(long time);
	public abstract String addNewConnecter();
	public String getConnecter(int count,long time) {
		if(count<0&&count>=connecterID.length)
			return "";
		return this.generateSign(time);
	}
	public long getTime() {
		return System.currentTimeMillis()/TIMEOUT;
	}
	public static SecurityLog createLog(String key) {
		String informations[]=null;
		SecurityLog log=null;
		if(key.equals("")) {
			log=new SHALog();
			return log;
		}else {
			informations=key.split("@");
			if(informations.length<2)
				return null;
		}
		if(informations[0].equals("SHA")) {
			log=new SHALog(informations[1]);
		}
		return log;
	}
}
