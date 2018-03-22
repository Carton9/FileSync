package com.net;

public abstract class TCPFrame{
	boolean isRecevie;
	int RequirePipeSize;
	ControlSocket loadedSocket;
	String dataPipeList[];
	boolean successInit;
	boolean successProcess=false;
	String frameType;
	TCPFrame(){
		isRecevie=true;
	}
	public String getFrameType() {
		return frameType;
	}
	public void execute() {
		
		if(isRecevie) 
			successProcess=this.recevie();	
		else
			successProcess=this.send();
		
	}
	public int getRequirePipeSize() {
		return RequirePipeSize;
	}
	public boolean init(String dataPipeList[],ControlSocket loadedSocket) {
		if(RequirePipeSize!=dataPipeList.length)
			return false;
		
		this.dataPipeList=dataPipeList;
		this.loadedSocket=loadedSocket;
		successInit=this.init();
		return successInit;
	}
	protected abstract boolean init();
	protected abstract boolean send();
	protected abstract boolean recevie();
}
