package com.net;

public abstract class TCPFrame{
	boolean isRecevie;
	int RequirePipeSize;
	ControlSocket loadedSocket;
	String dataPipeList[];
	boolean successInit;
	boolean successProcess=false;
	TCPFrame(){
		isRecevie=true;
		
	}
	public void execute() {
		if(isRecevie) 
			successProcess=this.recevie();	
		else
			successProcess=this.send();
	}
	protected int getRequirePipeSize() {
		return RequirePipeSize;
	}
	protected boolean init(String dataPipeList[],ControlSocket loadedSocket) {
		if(RequirePipeSize!=dataPipeList.length)
			return false;
		this.dataPipeList=dataPipeList;
		this.loadedSocket=loadedSocket;
		return this.init();
	}
	protected abstract boolean init();
	protected abstract boolean send();
	protected abstract boolean recevie();
}
