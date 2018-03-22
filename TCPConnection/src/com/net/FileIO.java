package com.net;

import java.io.File;

public interface FileIO {
	public void load(File file);
	public void loadTemp();
	public byte[] read(int size);
	public boolean write(byte data[]);
	public int read();
	public boolean write(byte data);
	public long fileSize();
}
