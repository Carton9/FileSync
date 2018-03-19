package com.carton.filesync.net;

import java.io.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import com.carton.filesync.common.util.Block;
import com.carton.filesync.common.util.ObjectLock;
import com.carton.filesync.core.Core;
import com.carton.filesync.file.FileIO;
import com.carton.filesync.file.UniversalFileIO;

public class TCPFileFrame extends TCPFrame {
	File file;
	FileIO io;
	protected TCPFileFrame() {
		super();
		file=null;
		io=null;
	}
	protected TCPFileFrame(File file) {
		this.file=file;
		io=null;
	}
	@Override
	protected boolean send() {
		// TODO Auto-generated method stub
		while(io==null)io=UniversalFileIO.getReadFileStream(file);
		for(int i=0;i<retry;i++) {
			try {
				boolean check=sendCommend(TCPState.CheckAliveRequest);
				if(check)check=sendCommend(TCPState.FileStreamRequst);
				if(check)check=sendCommend(TCPState.TransmissionBegin);
				if(check) {
					byte data[]=file.getAbsolutePath().getBytes();
					cos.writeInt(data.length);
					dos.write(data,0,data.length);
				}
				if(check)check=sendCommend(TCPState.TransmissionEnd);
				if(check)check=sendCommend(TCPState.TransmissionBegin);
				if(check) {
					if(io.getType()==-1) {
						Block data=io.read();
						while(data!=null) {
							byte sendAbleData[]=Core.getKey().encrypt(data.read());
							dos.write(sendAbleData, 0, sendAbleData.length);
							data=io.read();
						}
						sendCommend(TCPState.TransmissionEnd);
					}
					
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
		ObjectLock lock=new ObjectLock();
		Thread reply=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true) {
						replyInfo(TCPState.TransmissionWait,TCPState.TransmissionStandBy);
						Thread.sleep(1000);
					}
				}catch (InterruptedException e) {
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread revice=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true) {
						byte buff[]=new byte[20000000];
						int inputSize;
						lock.lock();
						inputSize=dis.read(buff);
						lock.unlock();
						if(inputSize!=-1) {
							buff=Core.getKey().decrypt(buff);
							io.write(buff);
						}
						Thread.sleep(1);
					}
				}catch (InterruptedException e) {
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		try {
			replyInfo(TCPState.TransmissionBegin,TCPState.TransmissionBeginConfirm);
			int dataLength=cis.readInt();
			byte[] dataBuff=new byte[dataLength];
			dis.read(dataBuff);
			replyInfo(TCPState.TransmissionEnd,TCPState.TransmissionEndConfirm);
			replyInfo(TCPState.TransmissionBegin,TCPState.TransmissionStandBy);
			reply.start();
			file=new File(new String(dataBuff));
			while(io==null)io=UniversalFileIO.getWriteFileStream(file);
			reply.interrupt();
			reply.stop();
			replyInfo(TCPState.TransmissionBegin,TCPState.TransmissionBeginConfirm);
			revice.start();
			replyInfo(TCPState.TransmissionEnd,TCPState.TransmissionEndConfirm);
			revice.interrupt();
			if(!lock.tryLock(2500, TimeUnit.MILLISECONDS))
				revice.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



}
