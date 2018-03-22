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
	File loadedFile=null;
	@Override
	public void load(File file) {
		// TODO Auto-generated method stub

	}
	@Override
	public void loadTemp() {
		try {
			loadedFile=File.createTempFile("Temp", ".tp");
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
			(new FileInputStream(loadedFile)).read(data);
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
			(new FileOutputStream(loadedFile)).write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	@Override
	public long fileSize() {
		// TODO Auto-generated method stub
		try {
			return (new FileOutputStream(loadedFile)).getChannel().size();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
		}
	}
	@Override
	public int read() {
		// TODO Auto-generated method stub
		try {
			return (new FileInputStream(loadedFile)).read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
		}
	}
	@Override
	public boolean write(byte data) {
		// TODO Auto-generated method stub
		try {
			(new FileOutputStream(loadedFile)).write(data);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}


}
