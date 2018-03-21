package com.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ObejctFrame<T> extends TCPFrame {
	T object;
	byte[] data;
	public ObejctFrame(T object) {
		this.object=object;
		this.RequirePipeSize=1;
	}
	public ObejctFrame() {
		super();
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
		HashMap<String,BiUnit<InputStream,OutputStream>> map=this.loadedSocket.loadPipes(dataPipeList);
		BiUnit<InputStream,OutputStream> unit=map.get(dataPipeList[0]);
		//DataOutputStream dos=new DataOutputStream()
		return false;
	}

	@Override
	protected boolean recevie() {
		
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
	private T Deserialize(byte[] input) throws IOException, ClassNotFoundException{
		ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(input);  
		ObjectInputStream objectOutputStream = new ObjectInputStream(byteArrayOutputStream);
		T output=(T)objectOutputStream.readObject();  
        objectOutputStream.close();        
        byteArrayOutputStream.close();  
        return output;
	}
}
