package com.carton.filesync.net;

public interface NetworkVerifier {
	public boolean veriftyID(String id);
	public boolean veriftyPort(int port);
}
