package com.carton.filesync.net;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.carton.filesync.file.FileIO;
import com.carton.filesync.file.UniversalFileIO;

public abstract class TCPFrame{
	protected boolean isRecevie;
	protected int RequirePipeSize;
	protected ControlSocket loadedSocket;
	protected String dataPipeList[];
	protected boolean finish=false;
	protected boolean successInit;
	protected boolean successProcess=false;
	protected String frameType;
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
		finish=true;
		synchronized(this) {
			this.notifyAll();
		}
		
	}
	public boolean isFinish() {return finish;}
	public int getRequirePipeSize() {
		return RequirePipeSize;
	}
	public boolean init(String dataPipeList[],ControlSocket loadedSocket) {
		if(RequirePipeSize>dataPipeList.length)
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
	public static TCPFrame createFrame(Serializable Object) {
		return new ObejctFrame(Object);
	}
	public static TCPFrame createFrame(FileIO Object) {
		if(Object.mappedMode())
			return new PTFrame(Object);
		else
			return new StreamFrame(Object);
	}
	public static<T> TCPFrame createFrame(File file) {
		UniversalFileIO io=new UniversalFileIO(file);
		if(io.mappedMode())
			return new PTFrame(io);
		else
			return new StreamFrame(io);
	}
	public static<T> T unpackPacket(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.PACKETFRAME)
			return null;
		while(!frame.isFinish()) {
			try {
				frame.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (T)frame.result();
	}
	public static FileIO unpackStream(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.STREAMFRAME)
			return null;
		while(!frame.isFinish()) {
			try {
				frame.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (FileIO)frame.result();
	}
	public static FileIO unpackPTStream(TCPFrame frame) {
		if(frame.getFrameType()!=ControlSocket.PTSTREAMFRAME)
			return null;
		while(!frame.isFinish()) {
			try {
				frame.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (FileIO)frame.result();
	}
	protected static FileIO getTempFile() {
		FileIO io=new UniversalFileIO();
		return io;
	}
}
