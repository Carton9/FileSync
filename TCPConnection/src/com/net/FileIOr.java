/**
 * 
 */
package com.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;

/**
 * @author mike
 *
 */
public class FileIOr implements FileIO {
	public File loadedFile=null;
	FileInputStream fis;
	FileOutputStream fos;
	@Override
	public void load(File file) {
		// TODO Auto-generated method stub
		try {
			this.loadedFile=file;
			fis=new FileInputStream(loadedFile);
			fos=new FileOutputStream(loadedFile,true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void loadTemp() {
		try {
			loadedFile=File.createTempFile("Temp", ".tp");
			System.out.print(loadedFile.getAbsolutePath());
			fis=new FileInputStream(loadedFile);
			fos=new FileOutputStream(loadedFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public byte[] read(int size) {
		// TODO Auto-generated method stub
		byte data[]=new byte[size];
		try {
			fis.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return data;
	}
	@Override
	public boolean write(byte[] data) {
		// TODO Auto-generated method stub
		try {
			fos.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	@Override
	public long fileSize() {
		// TODO Auto-generated method stub
		return loadedFile.length();
	}
	@Override
	public int read() {
		// TODO Auto-generated method stub
		try {
			return fis.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
		}
	}
	@Override
	public boolean write(byte data) {
		// TODO Auto-generated method stub
		try {
			fos.write(data);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}


}
