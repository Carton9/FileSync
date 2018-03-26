package com.carton.filesync.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.carton.filesync.common.util.BiUnit;
import com.carton.filesync.common.util.Block;
import com.carton.filesync.common.util.ObjectLock;
import com.carton.filesync.file.FileIO;

public class PTSubFrame extends TCPFrame {
	ObjectLock lock=new ObjectLock();
	FileIO io;
	Block data;
	int order;
	PTSubFrame(){
		order=-1;
		RequirePipeSize=1;
	}
	PTSubFrame(Block data,int order){
		this.data=data;
		this.order=order;
		isRecevie=false;
		RequirePipeSize=1;
	}
	@Override
	protected boolean init() {
		// TODO Auto-generated method stub
		if(isRecevie)
			io=super.getTempFile();
		lock.lock();
		this.successInit=true;
		return true;
	}

	@Override
	protected boolean send() {
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			DataOutputStream dos=new DataOutputStream(unit.getO());
			dos.writeInt(order);
			dos.writeInt(data.read().length);
			unit.getO().write(data.read());
			this.loadedSocket.closeSendingDataPipe(dataPipeList,false);
			lock.unlock();
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected boolean recevie() {
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			DataInputStream dis=new DataInputStream(unit.getK());
			this.order=dis.readInt();
			int dataSize=dis.readInt();
			byte[] data=new byte[dataSize];
			unit.getK().read(data);
			io.write(data);
			this.loadedSocket.closeReceiveDataPipe(dataPipeList,false);
			lock.unlock();
			//System.out.println(new String(io.read((int)io.fileSize())));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected Object result() {
		// TODO Auto-generated method stub
		return io;
	}
	protected void finishTrans() {
		while(lock.state()==true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
