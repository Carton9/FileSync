package com.carton.filesync.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;

import com.carton.filesync.common.util.SerializeUnit;
import com.carton.filesync.file.FileIO;

public abstract class TCPFrame {
	protected DataOutputStream cos;
	protected DataInputStream cis;
	protected OutputStream dos;
	protected InputStream dis;
	protected boolean isAvaliable;
	protected boolean isFinish;
	protected boolean isRevice;
	protected final static int retry=5;
	protected TCPFrame() {
		isRevice=true;
	}
	protected boolean sendCommend(TCPState state) throws IOException {
		int error=0;
		if(TCPState.getTCPStateByInt(state.getType()+1)!=null&&state.getType()%2==1) {
			int reply=0;
			int sended=0;
			while(true) {
				try {
					cos.writeInt(state.getType());
					reply=cis.readInt();
					TCPState replyState=TCPState.getTCPStateByInt(reply);
					if(reply==TCPState.TransmissionStandBy.getType()) {
						cos.writeInt(TCPState.TransmissionWait.getType());
						cos.flush();
						System.out.println("Stand by send:"+state.getType());
						System.out.println("reply:"+reply);
						continue;
					}else if(reply==TCPState.CommunicationError.getType()){
						reply=cis.readInt();
						replyState=TCPState.getTCPStateByInt(reply);
						System.out.println("reply:"+reply);
						if(reply==TCPState.getTCPStateByInt(state.getType()+1).getType()) 
							return true;
					}else if(reply==TCPState.getTCPStateByInt(state.getType()+1).getType()) {
						System.out.println("Correct send:"+state.getType());
						System.out.println("reply:"+reply);
						return true;
					}else {
						System.out.println("send:"+state.getType());
						System.out.println("reply:"+reply);
						boolean isCorrect=true;
						while(isCorrect) {
							reply=cis.readInt();
							replyState=TCPState.getTCPStateByInt(reply);
							System.out.println("reply:"+reply);
							if(reply==TCPState.getTCPStateByInt(state.getType()+1).getType()) 
								return true;
						}
						continue;
					}
				}catch(SocketTimeoutException e){
					if(sended>=retry)
						return false;
					else
						sended++;
				}
			}
		}else {
			cos.writeInt(state.getType());
			return true;
		}
	}
	public void send(DataOutputStream cos,DataInputStream cis,OutputStream dos,InputStream dis) {
		this.cos=cos;
		this.cis=cis;
		this.dis=dis;
		this.dos=dos;
		isAvaliable=true;
		isFinish=false;
		this.send();
		isAvaliable=false;
		isFinish=true;
	}
	public void revice(DataOutputStream cos,DataInputStream cis,OutputStream dos,InputStream dis) {
		this.cos=cos;
		this.cis=cis;
		this.dis=dis;
		this.dos=dos;
		isAvaliable=true;
		isFinish=false;
		this.revice();
		isAvaliable=false;
		isFinish=true;
	}
	public static<T extends SerializeUnit> TCPFrame getTCPFrame(T info) throws IOException {
		SerializeUnit unit=info;
		byte data[]=unit.Serialize();
		TCPPacketFrame frame=new TCPPacketFrame(data, info.getClass());
		return frame;
	}
	public static TCPFrame getTCPFrame(File sendFile) {
		try {
			long fileSize = FileIO.getSize(sendFile);
			TCPFrame frame=null;
			if(fileSize<Integer.MAX_VALUE-1000) {
				frame=new TCPFileFrame(sendFile);
			}else {
				//frame=new TCPParallelFrame(sendFile);
			}
			return frame;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public static TCPFrame getTCPFrame(TCPState type) {
		TCPFrame frame=null;
		if(type==TCPState.FilePacketRequst) {
			frame=new TCPPacketFrame();
		}else if(type==TCPState.FileStreamRequst) {
			frame=new TCPFileFrame();
		}else if(type==TCPState.PTStreamRequst) {
			frame=new TCPParallelFrame();
		}else if(type==TCPState.FileListReady) {
			frame=new TCPSyncFrame();
		}else
			;
		return frame;
	}
	/*protected boolean cofirmCommend(TCPState state) throws IOException {
		if(TCPState.getTCPStateByInt(state.getType()+1)!=null) {
			cos.writeInt(state.getType());
			int reply=0;
			while(true) {
				try {
					reply=cis.readInt();
					if(TCPState.getTCPStateByInt(reply)!=null&&TCPState.getTCPStateByInt(reply)==TCPState.getTCPStateByInt(state.getType()+1)) {
						return true;
					}else if(state==TCPState.TransmissionBegin&&TCPState.getTCPStateByInt(reply)==TCPState.TransmissionStandBy){
						continue;
					}else {
						return false;
					}
				}catch(SocketTimeoutException e){
					return false;
				}
			}
		}else{
			cos.writeInt(state.getType());
			return true;
		}
	}*/
	protected final TCPState filtedInfo() throws IOException {
		TCPState state=null;
		while(state==null){
			try {
				int reply=cis.readInt();
				state=TCPState.getTCPStateByInt(reply);
				if(state==state.CheckAliveRequest) {
					try {
						cos.writeInt(reply+1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(e);
						
					}
					state=null;
				}
			}catch(SocketTimeoutException e){
				state=null;
			}
		}
		return state;
	}
	protected void replyInfo(TCPState target,TCPState reply) throws IOException {
		while(true) {
			TCPState state=filtedInfo();
			if(state==target) {
				cos.writeInt(reply.getType());
				cos.flush();
				return;
			}
		}
	}
	protected abstract boolean send();
	protected abstract boolean revice();
}
