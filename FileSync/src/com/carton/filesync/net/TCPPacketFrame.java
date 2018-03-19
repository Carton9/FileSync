package com.carton.filesync.net;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.carton.filesync.core.Core;
import com.carton.filesync.service.FileListSyncUnit;

public class TCPPacketFrame extends TCPFrame {
	private byte data[];
	private String classObject;
	
	protected TCPPacketFrame(byte data[],Class classObject) {
		this.data=data;
		this.classObject=classObject.getName();
		
	}
	protected TCPPacketFrame() {
		super();
		this.data=null;
		this.classObject=null;
		
	}
	@Override
	protected boolean send() {
		for(int i=0;i<retry;i++) {
			try {
				
				boolean check=sendCommend(TCPState.CheckAliveRequest);
				
				if(check)check=sendCommend(TCPState.FilePacketRequst);
				if(check)check=sendCommend(TCPState.TransmissionBegin);
				if(check) {
					String className=classObject.toString();
					byte[] classNameString=className.getBytes();
					cos.writeInt(classNameString.length);
					dos.write(classNameString, 0, classNameString.length);
					cos.writeInt(data.length);
					dos.write(data, 0, data.length);
				}
				return true;
			}catch(SocketTimeoutException e){
				continue;
			}catch (IOException e) {
				return false;
			}
		}
		return false;
		
	}

	@Override
	protected boolean revice() {
		try {
			TCPState state=this.filtedInfo();
			if(state==TCPState.TransmissionBegin)
				sendCommend(TCPState.TransmissionBeginConfirm);
			int classLength=cis.readInt();
			byte[] classBuff=new byte[classLength];
			dis.read(classBuff);
			
			int dataLength=cis.readInt();
			byte[] dataBuff=new byte[dataLength];
			dis.read(dataBuff);
			classObject=new String(classBuff);
			data=dataBuff;
			Core.frameList.add(this);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
