package com.carton.filesync.net;

import java.io.IOException;
import java.net.InetAddress;

import com.cartion.filesync.security.DECKey;
import com.cartion.filesync.security.KeyUnit;

public class DuplexControlSocket {
	ControlSocket Ssocket;
	ControlSocket Csocket;
	Thread PortListener;
	Thread PipeListener;
	public DuplexControlSocket(InetAddress ip,int port,KeyUnit key) throws IOException, InterruptedException {
		Thread clientInit=null;
		Thread ServerInit=null;
		if(key==null) {
			clientInit=new Thread() {
				public void run() {
					try {
						Csocket=new ControlSocket(ip,port);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.print("Connector error "+e);
					}
				}
			};
			ServerInit=new Thread() {
				public void run() {
					try {
						Ssocket=new ControlSocket(port);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.print("Listener error "+e);
					}
				}
			};
		}else {
			clientInit=new Thread() {
				public void run() {
					try {
						Csocket=new SecurityControlSocket(ip,port,key);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.print("Connector error "+e);
					}
				}
			};
			ServerInit=new Thread() {
				public void run() {
					try {
						Ssocket=new SecurityControlSocket(port,key);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.print("Listener error "+e);
					}
				}
			};
		}
		clientInit.start();
		ServerInit.start();
		clientInit.join();
		ServerInit.join();
		PortListener=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenPort();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		PipeListener=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenControlPipe();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}
	public DuplexControlSocket(String ip,int port,KeyUnit key) throws IOException, InterruptedException {
		this(InetAddress.getByName(ip),port,key);
	}
	public DuplexControlSocket(String ip,int port) throws IOException, InterruptedException {
		this(ip,port,null);
	}
	public boolean submitFrame(TCPFrame frame) throws IOException {
		return Csocket.submitFrame(frame);
	}
	public ResultQueue getResultQueue() {
		return Ssocket.getResultQueue();
	}
}
