package com.carton.filesync.net;

import com.carton.filesync.net.NetworkVerifier;

public abstract class SecurityLog implements NetworkVerifier{
	protected static final int TIMEOUT=10*60*1000;//10 min
	protected String id;
	protected String connecterID[];
	protected int connecterCount;
	protected int portsLog[]=new int[65536];//0 not used,1 logged,-1 using
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
	public abstract String addNewConnecter();
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
