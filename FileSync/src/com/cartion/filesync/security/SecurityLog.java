package com.cartion.filesync.security;

import com.carton.filesync.net.NetworkVerifier;

public abstract class SecurityLog implements NetworkVerifier{
	protected String id;
	protected String connecterID[];
	protected int connecterCount;
	public abstract boolean isConnecter(String connecter);
	public abstract String generateSign();
	
}
