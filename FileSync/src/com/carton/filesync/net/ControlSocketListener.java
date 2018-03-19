package com.carton.filesync.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import com.carton.filesync.core.Core;

public class ControlSocketListener extends Thread {
	boolean alive;
	private ServerSocket server;
	private HashMap<InetAddress,Socket> controlWaittingList=new HashMap<InetAddress,Socket>();
	public ControlSocketListener(int port) throws IOException {
		server=new ServerSocket(port);
		alive=true;
	}
	public void run(){
		while(alive){
			Socket client = null;
			try {
				client=server.accept();
				
			}catch(SocketException e){
				System.out.println("server be Close");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(client!=null){
				System.out.print(client);
				try {
					int reply=new DataInputStream(client.getInputStream()).readInt();
					
					if(reply==TCPState.ControlPipeConnect.getType()){
						controlWaittingList.put(client.getInetAddress(), client);
						(new DataOutputStream(client.getOutputStream())).writeInt(TCPState.ControlPipedConnected.getType());
						
					}else if(reply==TCPState.DataPipeConnect.getType()) {
						
						if(controlWaittingList.containsKey(client.getInetAddress())) {
							ControlSocket socket=new ControlSocket(controlWaittingList.remove(client.getInetAddress()),client);
							Core.addNewSocket(socket);
							socket.start();
							(new DataOutputStream(client.getOutputStream())).writeInt(TCPState.DataPipedConnected.getType());
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Server Close");
	}
	public void close(){
		this.alive=false;
		try {
			this.server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
