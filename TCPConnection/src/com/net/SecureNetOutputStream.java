package com.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SecureNetOutputStream extends OutputStream {
	OutputStream Output;KeyUnit unit;
	SecureNetOutputStream(OutputStream Output,KeyUnit unit){
		this.Output=Output;
		this.unit=unit;
	}
	@Override
	public void write(int arg0) throws IOException {
		// TODO Auto-generated method stub
		Output.write(unit.encrypt(new byte[] {(byte) arg0}));
	}
	@Override
	public void write(byte[] arg0) throws IOException {
		// TODO Auto-generated method stub
		Output.write(unit.encrypt(arg0));
	}
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		byte code[]=new byte[len];
		for(int i=off;i<len;i++) {
			code[i-off]=b[i];
		}
		Output.write(unit.encrypt(code));
	}

}
