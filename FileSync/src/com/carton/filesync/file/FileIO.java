package com.carton.filesync.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.carton.filesync.common.util.Block;

public class FileIO {
	MappedBiggerFileReader reader=null;
	FileInputStream input=null;
	FileOutputStream output=null;
	File file;
	private int type;
	public FileIO(File file,boolean read) {
		try {
			if(read&&getSize(file)>500000000) {
				reader=new MappedBiggerFileReader(file,20000000);
				type=-1;
			}else if(read){
				input=new FileInputStream(file);
				type=0;
			}else {
				output=new FileOutputStream(file);
				type=1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getType() {
		return type;
	}
	public static long getSize(File file) throws IOException {
		FileInputStream fileIn = new FileInputStream(file);
        FileChannel fileChannel = fileIn.getChannel();
		return fileChannel.size();
	}
	public int read(byte[] buff) {
		try {
			if(input!=null)
				return input.read(buff);
			else return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	public Block read() throws IOException {
		if(reader!=null)
			return reader.syncRead();
		else return null;
	}
	public void write(byte[] buff) throws IOException {
		if(output!=null)
			output.write(buff);
	}
	public void write(byte[] buff,int size) throws IOException {
		if(output!=null)
			output.write(buff,0,size);
	}
	public void close() {
		try {
			if(output!=null)
				output.close();
			if(reader!=null)
				reader.close();
			if(input!=null)
				input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
