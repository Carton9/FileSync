package com.carton.filesync.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Executors;

import com.cartion.filesync.security.DECKey;
import com.cartion.filesync.security.KeyUnit;
import com.carton.filesync.common.util.BiUnit;

public class SecurityControlSocket extends ControlSocket{
	KeyUnit key;
	public SecurityControlSocket(String ip,int port,KeyUnit key) throws IOException {
		super(ip,port);
		this.key=key;
	}
	public SecurityControlSocket(InetAddress ip,int port,KeyUnit key) throws IOException {
		super(ip,port);
		this.key=key;
	}
	public SecurityControlSocket(int port,KeyUnit key) throws IOException {
		super(port);
		this.key=key;
	}
	public SecurityControlSocket(InetAddress ip,int port) throws IOException {
		super(ip,port);
		key=DECKey.getdefultKey();
	}
	public SecurityControlSocket(int port) throws IOException {
		super(port);
		key=DECKey.getdefultKey();
	}
	public SecurityControlSocket(String ip,int port) throws IOException {
		super(ip,port);
		key=DECKey.getdefultKey();
	}
	public HashMap<String,BiUnit<InputStream,OutputStream>> loadSecurePipes(String[] keys,KeyUnit keyUnit) throws IOException{
		HashMap<String,BiUnit<InputStream,OutputStream>> rawdata=super.loadPipes(keys);
		HashMap<String,BiUnit<InputStream,OutputStream>> data=new HashMap<String,BiUnit<InputStream,OutputStream>>();
		for(String i:rawdata.keySet()) {
			BiUnit<InputStream,OutputStream> unit=rawdata.get(i);
			unit.setK(new SecureNetInputStream(unit.getK(),key));
			unit.setO(new SecureNetOutputStream(unit.getO(),key));
			data.put(i, unit);
		}
		return data;
	}
}
