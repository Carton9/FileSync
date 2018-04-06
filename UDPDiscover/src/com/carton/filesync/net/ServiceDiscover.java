package com.carton.filesync.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceDiscover {
	int sendPort=29999;
	int receviePort=29998;
	InetAddress locoal;
	DatagramSocket ds;
	DatagramPacket dp_send;
	DatagramPacket dp_receive;
	Timer timer;
	boolean isServer;
	byte[] data;
	int port;
	TimerTask sendInfo;
	private void createDatagram() {
		
	}
}
