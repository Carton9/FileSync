package com.carton.filesync.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.carton.filesync.common.util.BiUnit;

public class ObejctFrame extends TCPFrame {
	Serializable object;
	byte[] data;
	public ObejctFrame(Serializable object) {
		this.frameType=ControlSocket.PACKETFRAME;
		this.object=object;
		this.RequirePipeSize=1;
		this.isRecevie=false;
	}
	public ObejctFrame() {
		super();
		this.frameType=ControlSocket.PACKETFRAME;
		this.RequirePipeSize=1;
	}
	@Override
	protected boolean init() {
		try {
			data=Serialize();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean send() {
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			DataOutputStream dos=new DataOutputStream(unit.getO());
			System.out.print(data.length);
			dos.writeInt(data.length);
			unit.getO().write(data);
			this.loadedSocket.closeSendingDataPipe(dataPipeList);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected boolean recevie(){
		try {
			HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
			BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
			DataInputStream dis=new DataInputStream(unit.getK());
			int dataSize=dis.readInt();
			this.data=new byte[dataSize];
			unit.getK().read(data);
			try {
				System.out.print(Deserialize(data).getClass());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.loadedSocket.closeReceiveDataPipe(dataPipeList);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private byte[] Serialize() throws IOException{
		byte[] output=null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);       
		objectOutputStream.flush();        
		output = byteArrayOutputStream.toByteArray ();     
        objectOutputStream.close();        
        byteArrayOutputStream.close();  
        return output;
	}
	private Serializable Deserialize(byte[] input) throws IOException, ClassNotFoundException{
		ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(input);  
		ObjectInputStream objectOutputStream = new ObjectInputStream(byteArrayOutputStream);
		Serializable output=(Serializable)objectOutputStream.readObject();  
        objectOutputStream.close();        
        byteArrayOutputStream.close();  
        return output;
	}
	@Override
	protected Object result() {
		return object;
	}
}
