package com.net;

import java.io.File;

public interface FileIO {
	public void load(File file);
	public void loadTemp();
	public byte[] read(int size);
	public boolean write(byte data[]);
	public boolean write(byte data[],int size);
	public int read();
	public boolean write(byte data);
	public long fileSize();
	public Block getBlock();
	public boolean mappedMode();
	public int BlockCount();
}
