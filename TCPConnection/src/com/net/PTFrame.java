package com.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PTFrame extends TCPFrame {
	FileIO readIO;
	ArrayList<PTSubFrame> resultList;
	ArrayList<TCPFrame> subList;
	static final String TransmissionBegin="TSMB";
	static final String TransmissionEnd="TSME";
	PTFrame(){
		super();
		this.frameType=ControlSocket.PTSTREAMFRAME;
		resultList=new ArrayList<PTSubFrame>();
		subList=new ArrayList<TCPFrame>();
		isRecevie=true;
	}
	PTFrame(FileIO file){
		readIO=file;
		this.frameType=ControlSocket.PTSTREAMFRAME;
		this.RequirePipeSize=(int) (readIO.fileSize()/10240)+2;
		this.isRecevie=false;
		resultList=new ArrayList<PTSubFrame>();
		subList=new ArrayList<TCPFrame>();
	}
	
	@Override
	protected boolean init() {
		if(isRecevie){
			this.RequirePipeSize=this.dataPipeList.length;
			for(int i=1;i<RequirePipeSize;i++) {
				PTSubFrame subFrame=new PTSubFrame();
				boolean result=subFrame.init(new String[] {dataPipeList[i]}, loadedSocket);
				System.out.println("isRecevie "+result);
				subList.add(subFrame);
				resultList.add(subFrame);
			}
		}else {
			for(int i=1;i<RequirePipeSize;i++) {
				PTSubFrame subFrame=new PTSubFrame(readIO.getBlock(),i);
				boolean result=subFrame.init(new String[] {dataPipeList[i]}, loadedSocket);
				System.out.println("isRecevie "+result);
				subList.add(subFrame);
				resultList.add(subFrame);
			}
		}
		
		this.successInit=true;
		return true;
	}

	@Override
	protected boolean send() {
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			unit.getO().write(TransmissionBegin.getBytes());
			boolean result=loadedSocket.loadRunnableFrames(subList);
			
			for(PTSubFrame i:resultList) {
				i.finishTrans();
			}
			unit.getO().write(TransmissionEnd.getBytes());
			this.loadedSocket.closeSendingDataPipe(new String[] {dataPipeList[0]});
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
			String commend=recevieCommend(unit.getK());
			if(commend.equals(TransmissionBegin)) {
				loadedSocket.loadRunnableFrames(subList);
			}
			for(PTSubFrame i:resultList) {
				i.finishTrans();
			}
			commend=recevieCommend(unit.getK());
			if(commend.equals(TransmissionEnd)) {	
			}
			this.loadedSocket.closeReceiveDataPipe(new String[] {dataPipeList[0]});
			System.out.println();
			for(PTSubFrame i:resultList) {
				System.out.print((int)i.io.fileSize());
			}
			System.out.println();
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
		return false;
	}
	private String recevieCommend(InputStream stream) throws IOException {
		byte[] recevie=new byte[4];
		stream.read(recevie);
		return new String(recevie);
	}
}
