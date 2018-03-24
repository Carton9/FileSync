package com.net;

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

public class SecurityControlSocket extends ControlSocket{

	public SecurityControlSocket(InetAddress ip,int port) throws IOException {
		super(ip,port);
	}
	public SecurityControlSocket(int port) throws IOException {
		super(port);
	}
	public SecurityControlSocket(String ip,int port) throws IOException {
		super(ip,port);
	}
	public HashMap<String,BiUnit<InputStream,OutputStream>> loadSecurePipes(String[] keys,KeyUnit keyUnit) throws IOException{
		HashMap<String,BiUnit<InputStream,OutputStream>> rawdata=super.loadPipes(keys);
		HashMap<String,BiUnit<InputStream,OutputStream>> data=new HashMap<String,BiUnit<InputStream,OutputStream>>();
		for(String i:rawdata.keySet()) {
			BiUnit<InputStream,OutputStream> unit=rawdata.get(i);
			unit.setK(new SecureNetInputStream(unit.getK(),new DECKey()));
			unit.setO(new SecureNetOutputStream(unit.getO(),new DECKey()));
			data.put(i, unit);
		}
		return data;
	}
}
