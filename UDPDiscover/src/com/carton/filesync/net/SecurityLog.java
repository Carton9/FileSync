package com.carton.filesync.net;

import com.carton.filesync.net.NetworkVerifier;

public abstract class SecurityLog implements NetworkVerifier{
	protected static final int TIMEOUT=10*60*1000;//10 min
	protected String id;
	protected String connecterID[];
	protected int connecterCount;
	protected int portsLog[]=new int[65536];//0 not used,1 logged,-1 using
	public abstract boolean isConnecter(String connecter);
	public boolean veriftyPort(int port) {
		if(portsLog[port]==0)
			return true;
		return false;
	}
	public boolean logPort(int port) {
		if(veriftyPort(port))
			portsLog[port]=1;
	}
	public boolean logUsingPort(int port) {
		if(veriftyPort(port))
			portsLog[port]=1;
	}
	public abstract String generateSign();
	public abstract String addNewConnecter();
	
}
