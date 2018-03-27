package com.carton.filesync.service;

import java.util.HashMap;

import com.carton.filesync.net.ControlSocket;
import com.carton.filesync.net.SecurityControlSocket;

public class NetWorkSocketManager {
	HashMap<String,ControlSocket> controlSocketMap;
	HashMap<String,SecurityControlSocket> secureControlSocketMap;
	boolean secureMode;
	public NetWorkSocketManager(boolean secureMode) {
		this.secureMode=secureMode;
		if(secureMode)
			secureControlSocketMap=new HashMap<String,SecurityControlSocket>();
		else
			controlSocketMap=new HashMap<String,ControlSocket>();
	}
	public boolean addControlSocket(String id,SecurityControlSocket socket) {
		if(secureMode){
			secureControlSocketMap.put(id, socket);
			return true;
		}
		return false;
	}
	public boolean addControlSocket(String id,ControlSocket socket) {
		if(!secureMode){
			controlSocketMap.put(id, socket);
			return true;
		}
		return false;
	}
	public boolean removeControlSocket(String id) {
		if(secureMode){
			if(secureControlSocketMap!=null&&secureControlSocketMap.get(id)!=null)
				secureControlSocketMap.remove(id).close();
			else
				return false;
		}
		else{
			if(controlSocketMap!=null&&controlSocketMap.get(id)!=null)
				controlSocketMap.remove(id).close();
			else
				return false;
		}
		return true;
	}
	public ControlSocket getSocket(String id) {
		if(secureMode)
			return secureControlSocketMap.get(id);
		else
			return controlSocketMap.get(id);
	}
}
