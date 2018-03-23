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
public class UniversalFileIO implements FileIO {
	public File loadedFile=null;
	private int blockSize;
	FileInputStream fis;
	FileOutputStream fos;
	MappedBiggerFileReader reader;
	boolean mappedMode;
	static int defultSize=100;
	public UniversalFileIO() {
		loadTemp();
		mappedMode=false;
	}
	public UniversalFileIO(File file) {
		this(file,defultSize);
	}
	public UniversalFileIO(File file,int blockSize) {
		this.loadedFile=file;
		this.blockSize=blockSize;
		if(file.length()>blockSize) {
			try {
				reader=new MappedBiggerFileReader(file, blockSize);
				mappedMode=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public UniversalFileIO(File file,int blockSize,boolean mapMode) {
		this.loadedFile=file;
		this.blockSize=blockSize;
		if(mapMode) {
			try {
				reader=new MappedBiggerFileReader(file, blockSize);
				mappedMode=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void load(File file) {
		// TODO Auto-generated method stub
		if(mappedMode)
			return;
		if(loadedFile!=null)
			loadedFile.delete();
		try {
			this.loadedFile=file;
			fis=new FileInputStream(loadedFile);
			fos=new FileOutputStream(loadedFile,true);
			blockSize=-1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void loadTemp() {
		if(mappedMode)
			return;
		if(loadedFile!=null)
			loadedFile.delete();
		try {
			loadedFile=File.createTempFile("Temp", ".tp");
			System.out.print(loadedFile.getAbsolutePath());
			fis=new FileInputStream(loadedFile);
			fos=new FileOutputStream(loadedFile);
			blockSize=-1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public byte[] read(int size) {
		// TODO Auto-generated method stub
		if(mappedMode)
			return null;
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
		if(mappedMode)
			return false;
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
		if(mappedMode)
			return -1;
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
		if(mappedMode)
			return false;
		try {
			fos.write(data);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	@Override
	public boolean write(byte[] data, int size) {
		// TODO Auto-generated method stub
		if(mappedMode)
			return false;
		try {
			fos.write(data,0,size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	@Override
	public Block getBlock() {
		// TODO Auto-generated method stub
		if(mappedMode&&reader!=null)
			return reader.syncRead();
		return null;
	}
	@Override
	public boolean mappedMode() {
		// TODO Auto-generated method stub
		return mappedMode;
	}
	@Override
	public int BlockCount() {
		// TODO Auto-generated method stub
		if(mappedMode&&reader!=null){
			return this.reader.blockCount();
		}
		return -1;
	}


}
