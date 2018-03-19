package com.carton.filesync.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class SerializeUnit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public <T> byte[] Serialize() throws IOException{
		byte[] output=null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(this);       
		objectOutputStream.flush();        
		output = byteArrayOutputStream.toByteArray ();     
        objectOutputStream.close();        
        byteArrayOutputStream.close();  
        return output;
	}
	public static<T> T Deserialize(byte[] input) throws IOException, ClassNotFoundException{
		ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(input);  
		ObjectInputStream objectOutputStream = new ObjectInputStream(byteArrayOutputStream);
		T output=(T)objectOutputStream.readObject();  
        objectOutputStream.close();        
        byteArrayOutputStream.close();  
        return output;
	}
}
