package com.carton.filesync.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.cartion.filesync.security.AuthorityKey;
import com.carton.filesync.common.util.BiUnit;
import com.carton.filesync.common.util.DoubleIndexMap;
import com.carton.filesync.net.ControlSocket;
import com.carton.filesync.net.TCPFrame;

public class Core {
	private static AuthorityKey key;
	public  static ArrayList<ControlSocket> unknowSocketList=new ArrayList<ControlSocket>();
	private static DoubleIndexMap<String,InetAddress> index=new DoubleIndexMap<String,InetAddress>();
	public static ArrayList<TCPFrame> frameList=new ArrayList<TCPFrame>(); 
	private static Thread controlSocketComplier=new Thread() {
		public void run() {
			if(!unknowSocketList.isEmpty());
		}
	};
	public static AuthorityKey getKey() {
		return key;
	}
	public static void addNewSocket(ControlSocket socket) {
		synchronized(unknowSocketList) {
			unknowSocketList.add(socket);
			unknowSocketList.notifyAll();
		}
	}
	public static void addRemoteMachine(BiUnit<String,InetAddress> info) {
		synchronized(index) {
			index.add(info);
		}
	}
	
}
