package com.carton.filesync.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class CompilableDatagram{
	InetAddress ipAddress;
	int portInfo;
	String idInfo;
	char serverMarkInfo;
	char secureMark;
	String versionInfo;
	static int defultDataLength;
	int dataLength;
	int readLengths[];
	static int defultReadLengths[]= {4,2,1,128,1,4};
	public CompilableDatagram() {
		dataLength=-1;
		defultDataLength=0;
		for(int i:defultReadLengths)
			defultDataLength+=i;
	}
	public CompilableDatagram(int idLength) {
		dataLength=0;
		readLengths[3]=idLength;
		for(int i:readLengths)
			dataLength+=i;
	}
	public static int getDataLength() {
		dataLength=0;
		for(int i:readLengths)
			dataLength+=i;
		return dataLength;
	}
	public static CompilableDatagram getDataByModule() {
		return null;
	}
	public byte[] compileInformation() throws IOException {
		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
		bOut.write(InetAddress.getLocalHost().getAddress());
		byte[] port=new byte[2];
		port[0]=(byte)((portInfo >>> 8) & 0xFF);
		port[1]=(byte)((portInfo >>> 0) & 0xFF);
		bOut.write(port);
		bOut.write(serverMarkInfo);
		bOut.write(idInfo.getBytes());
		bOut.write(secureMark);
		bOut.write(versionInfo.getBytes());
		byte[] data=bOut.toByteArray();
		if(data.length==this.dataLength)
			return data;
		else return null;
	}
	public static CompilableDatagram decompileData(byte[] data) throws IOException {
		ByteArrayInputStream bis=new ByteArrayInputStream(data);
		if(data.length!=dataLength)return null;
		byte ip[]=new byte[readLengths[0]];
		byte port[]=new byte[readLengths[1]];
		char serverMark=0;
		byte id[]=new byte[readLengths[3]];
		char secureMark=0;
		byte version[]=new byte[readLengths[5]];
		bis.read(ip);
		bis.read(port);
		serverMark=(char)bis.read();
		bis.read(id);
		secureMark=(char)bis.read();
		bis.read(version);

		InetAddress ipAddress=InetAddress.getByAddress(ip);
		int portInfo=(port[0] << 8) + (port[1] << 0);
		String idInfo=new String(id);
		String versionInfo=new String(version);
		CompilableDatagram datagram=new CompilableDatagram();
		datagram.ipAddress=ipAddress;
		datagram.idInfo=idInfo;
		datagram.portInfo=portInfo;
		datagram.serverMarkInfo=serverMark;
		datagram.secureMark=secureMark;
		datagram.versionInfo=versionInfo;
		return datagram;
	}
}
