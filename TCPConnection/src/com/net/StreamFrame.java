package com.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class StreamFrame extends TCPFrame {
	FileIO io;
	private final static String IntegerType="INTE";
	private final static String LongType="LONG";
	public StreamFrame() {
		super();
		this.frameType=ControlSocket.STREAMFRAME;
		this.RequirePipeSize=1;
	}
	public StreamFrame(FileIO io) {
		this.io=io;
		this.frameType=ControlSocket.STREAMFRAME;
		this.RequirePipeSize=1;
		this.isRecevie=false;
	}
	@Override
	protected boolean init() {
		if(isRecevie)
			io=super.getTempFile();
		return false;
	}

	@Override
	protected boolean send() {
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			
			DataOutputStream dos=new DataOutputStream(unit.getO());
			long size=io.fileSize();
			if(size<Integer.MAX_VALUE) {
				unit.getO().write(IntegerType.getBytes());
				dos.writeInt((int)size);
			}else {
				unit.getO().write(LongType.getBytes());
				dos.writeLong(size);
			}
			int result=io.read();
			while(result!=-1) {
				unit.getO().write(result);
				result=io.read();
			}
			this.loadedSocket.closeSendingDataPipe(dataPipeList);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected boolean recevie() {
		// TODO Auto-generated method stub
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			DataInputStream dis=new DataInputStream(unit.getK());
			String commend=recevieCommend(unit.getK());
			long dataLength=0;
			if(commend.equals(IntegerType)) {
				dataLength+=dis.readInt();
			}else if(commend.equals(LongType)) {
				dataLength+=dis.readLong();
			}
			if(dataLength>0) {
				for(long i=0;i<dataLength;i++) {
					int result=unit.getK().read();
					if(result>-1)
						io.write((byte)result);
				}
			}
			this.loadedSocket.closeReceiveDataPipe(dataPipeList);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private String recevieCommend(InputStream stream) throws IOException {
		byte[] recevie=new byte[4];
		stream.read(recevie);
		return new String(recevie);
	}
	@Override
	protected Object result() {
		// TODO Auto-generated method stub
		return io;
	}
	
}
