package com.carton.filesync.net;

import java.io.IOException;
import java.io.InputStream;

import com.cartion.filesync.security.KeyUnit;

public class SecureNetInputStream extends InputStream {
	InputStream input;
	KeyUnit unit;
	SecureNetInputStream(InputStream input,KeyUnit unit){
		this.input=input;
		this.unit=unit;
	}
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		byte[] codes=new byte[unit.decryptSize(1)];
		input.read(codes);
		codes=unit.decrypt(codes);
		return codes[0];
	}
	public int read(byte[] b)throws IOException {
		byte[] codes=new byte[unit.decryptSize(b.length)];
		input.read(codes);
		codes=unit.decrypt(codes);
		for(int i=0;i<b.length;i++) {
			b[i]=codes[i];
		}
		return 0;
	}
	public int read(byte[] b, int off, int len)throws IOException {
		byte[] codes=new byte[unit.decryptSize(len)];
		input.read(codes);
		codes=unit.decrypt(codes);
		for(int i=off;i<len;i++) {
			b[i]=codes[i];
		}
		return 0;
	}
	public void close()throws IOException {
		input.close();
		super.close();
	}
}
