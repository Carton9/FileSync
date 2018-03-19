package com.carton.filesync.file;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class FileWatcherConnecter extends Thread{
	DatagramSocket ds;
	DatagramPacket dp_receive;
	DatagramPacket dp_send;
	boolean alive;
	private static final int TIMEOUT = 5000;
	private static final int BUFFSIZE = 5000;
	FileEventComplier complier;
	public FileWatcherConnecter(int port,FileEventComplier complier) throws SocketException {
		ds = new DatagramSocket(port);
		ds.setSoTimeout(TIMEOUT); 
		this.complier=complier;
		alive=true;
	}
	public void run() {
		if(ds==null)
			return;
		byte[] buff=new byte[BUFFSIZE];
		dp_receive = new DatagramPacket(buff,BUFFSIZE);
		boolean getSignal=false;
		boolean IOError=false;
		while((!getSignal||!IOError)&&alive) {
			try {
				ds.receive(dp_receive);// data packet design: <change type>|<File path>
			}catch(SocketTimeoutException o) {
				System.out.println("time out");
				continue;
			}catch (IOException e) {
				IOError=true;
			}
			String buffs="";
			buffs+=new String(dp_receive.getData());
			buffs=buffs.trim();
			String buffString="";
			for(int i=0;i<buffs.length();i++) {
				buffString+=(char)(dp_receive.getData()[i]);
			}
			String changeFile[]=buffString.split("@");
			System.out.println(buffString);
			if(changeFile.length>=2) {
				complier.addRaw(changeFile[0], changeFile[1]);
				System.out.println(changeFile.length);
			}
			buff=new byte[BUFFSIZE];
			dp_receive = new DatagramPacket(buff,BUFFSIZE);
		}
		
	}
}
