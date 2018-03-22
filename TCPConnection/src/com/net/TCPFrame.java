package com.net;

import java.io.File;
import java.io.IOException;

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
	protected abstract Object result();
	public static TCPFrame createFrame(String commend) {
		if(commend.length()>4)
			return null;
		if(commend.equals(ControlSocket.PACKETFRAME)) {
			return new ObejctFrame();
		}
		if(commend.equals(ControlSocket.PTSTREAMFRAME)) {
			return new PTFrame();
		}
		if(commend.equals(ControlSocket.STREAMFRAME)) {
			return new StreamFrame();
		}
		return null;
	}
	public static<T> TCPFrame createFrame(T Object) {
		return new ObejctFrame<T>(Object);
	}
	public static<T> TCPFrame createFrame(FileIO Object) {
		return new StreamFrame(Object);
	}
	public static<T> T unpackPacket(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.PACKETFRAME)
			return null;
		return (T)frame.result();
	}
	public static FileIO unpackStream(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.STREAMFRAME)
			return null;
		return (FileIO)frame.result();
	}
	public static FileIO unpackPTStream(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.PTSTREAMFRAME)
			return null;
		return (FileIO)frame.result();
	}
	protected static FileIO getTempFile() {
		FileIO io=new FileIOr();
		io.loadTemp();
		return io;
	}
}
