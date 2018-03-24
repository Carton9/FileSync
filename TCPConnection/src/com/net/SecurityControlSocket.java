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
		return rawdata;
	}
}
