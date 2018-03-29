package com.carton.filesync.service;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import com.carton.filesync.file.FileIO;
import com.carton.filesync.net.ControlSocket;
import com.carton.filesync.net.DuplexControlSocket;
import com.carton.filesync.net.SecurityControlSocket;
import com.carton.filesync.net.TCPFrame;

public class NetWorkSocketManager {
	ConcurrentHashMap<String,DuplexControlSocket> controlSocketMap;
	public NetWorkSocketManager() {
	}
	public boolean addControlSocket(String id,DuplexControlSocket socket) {
		controlSocketMap.put(id, socket);
		return true;
	}
	public String[] keys() {
		return controlSocketMap.keySet().toArray(new String[controlSocketMap.size()]);
	}
	public DuplexControlSocket removeControlSocket(String id) {
		return controlSocketMap.remove(id);
	}
	public DuplexControlSocket getSocket(String id) {
		return controlSocketMap.get(id);
	}
	public<T> void submitFrameThough(String id,T object) {
		TCPFrame frame=null;
		if(object.getClass().equals(FileIO.class))
			frame=TCPFrame.createFrame((FileIO)object);
		else if(object.getClass().equals(File.class))
			frame=TCPFrame.createFrame((File)object);
		else
			frame=TCPFrame.createFrame(object);
	}
	public void closeControlSocket(String id) throws Exception {
		controlSocketMap.get(id).close();
		controlSocketMap.remove(id);
	}
	public void closeAllControlSocket() {
		String keys[]=keys();
		for(String i:keys) {
			try {
				closeControlSocket(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
