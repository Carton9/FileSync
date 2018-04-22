package com.carton.filesync.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.carton.filesync.common.util.GeneralManager;

public class NetworkManager extends GeneralManager {
	protected HashMap<String,MachineRecord> machineMap;
	public NetworkManager() {
		machineMap=new HashMap<String,MachineRecord>();
	}
	public boolean logMachine(MachineRecord record) {
		
		//System.out.println(machineMap.size());
		synchronized(machineMap) {
			//System.out.println(machineMap.size());
			for(String i:machineMap.keySet()) {
				if(i.equals(record.getId()))
					return false;
			}
			machineMap.put(record.getId(), record);
		//	System.out.println("get " +machineMap.size());
		}
		return true;
	}
	public boolean logMachine(InetAddress ip,int port,String id,long time) {
		return logMachine(new MachineRecord(ip,port,id,time));
	}
	public MachineRecord getRecord(String id) {
		synchronized(machineMap) {return machineMap.get(id);}
		}
	public boolean isLogged(String id) {
		//System.out.println("size "+machineMap.size());
		return machineMap.containsKey(id);
	}
	public boolean removeMachine(String id) {
		return machineMap.remove(id) != null;
	}
	@Override
	public <T extends GeneralManager> T loadManager(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T extends GeneralManager> byte[] saveManager(T Manager) {
		// TODO Auto-generated method stub
		return null;
	}
}
