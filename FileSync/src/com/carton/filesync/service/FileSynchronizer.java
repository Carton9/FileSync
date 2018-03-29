package com.carton.filesync.service;

import java.util.Vector;

import com.carton.filesync.file.FileEventComplier;
import com.carton.filesync.net.TCPFrame;

public class FileSynchronizer {
	Thread compileFileEvent;
	boolean compileFileEventAlive;
	FileEventComplier localMachine;
	NetWorkSocketManager netWork;
	String[] clientList;
	String Servers;
	Vector<String> queue=new Vector<String>();
	public FileSynchronizer(FileEventComplier localMachine,NetWorkSocketManager netWork,String[] clientList,String Servers) {
		compileFileEventAlive=true;
		compileFileEvent=new Thread() {
			public void run() {
				while(compileFileEventAlive) {
					localMachine.complie();
					if(this.interrupted()) {
						try {
							localMachine.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		this.localMachine=localMachine;
		this.netWork=netWork;
	}
	public FileSynchronizer(FileEventComplier localMachine,String[] clientList) {
		this(localMachine,null,clientList,null);
		this.netWork=new NetWorkSocketManager();
	}
	public FileSynchronizer(FileEventComplier localMachine,String Servers) {
		this(localMachine,null,null,Servers);
		this.netWork=new NetWorkSocketManager();
	}
	public boolean upGradeClientList(String[] list) {
		if(clientList!=null) {
			this.clientList=list;
			return true;
		}
		return false;
	}
	public void execute() {
		
	}
}
