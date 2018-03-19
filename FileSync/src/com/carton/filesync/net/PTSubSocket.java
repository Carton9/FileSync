package com.carton.filesync.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import com.carton.filesync.common.util.Block;

public class PTSubSocket extends Thread {
	Socket socket;
	int order;
	ArrayList<Block> unitList;
	int count=0;
	boolean finishTransmission;
	public PTSubSocket(Socket socket,int order) {
		this.socket=socket;
		this.order=order;
		unitList=null;
		finishTransmission=false;
	}
	public void addBlock(List<Block> list) {
		if(unitList==null)
			unitList=new ArrayList<Block>();
		unitList.addAll(list);
	}
	public void addBlock(Block unit) {
		if(unitList==null)
			unitList=new ArrayList<Block>();
		unitList.add(unit);
	}
	public void run() {
		try {
			if(unitList==null) {
				unitList=new ArrayList<Block>();
				DataInputStream dis=new DataInputStream(socket.getInputStream());
				for(int i=0;i<count;i++) {
					int dataLength=dis.readInt();
					byte[] data=new byte[dataLength];
					socket.getInputStream().read(data);
					unitList.add(new Block(data,order));
				}
				finishTransmission=true;
			}else {
				DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
				int size=unitList.size();
				for(int i=0;i<size;i++) {
					byte[] data=unitList.remove(0).read();
					dos.writeInt(data.length);
					socket.getOutputStream().write(data);
				}
				finishTransmission=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setBlockCount(int count) {
		this.count=count;
	}
	public int PerformOderCheck(boolean isServer,int order) throws IOException {
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		if(isServer) {
			int result=dis.readInt();
			dos.writeInt(order);
			return result;
		}else {
			dos.writeInt(order);
			int result=dis.readInt();
			return result;
		}
	}
}
