package com.carton.filesync.net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import com.carton.filesync.file.FileIO;

public class TCPParallelFrame extends TCPFrame {
	File file;
	FileIO io;
	protected TCPParallelFrame() {
		super();
		file=null;
		io=null;
		
		
	}
	protected TCPParallelFrame(File file) {
		this.file=file;
		io=null;
	}
	@Override
	protected boolean send() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean revice() {
		// TODO Auto-generated method stub
		return false;
	}


}
