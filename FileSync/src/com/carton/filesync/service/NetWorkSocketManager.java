package com.carton.filesync.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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
	public void submitFrameThough(String id,TCPFrame frame) throws IOException {
		controlSocketMap.get(id).submitFrame(frame);
	}
	public void submitFrameThough(String id,List<TCPFrame> frames) throws IOException {
		controlSocketMap.get(id).submitFrame(frames);
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
