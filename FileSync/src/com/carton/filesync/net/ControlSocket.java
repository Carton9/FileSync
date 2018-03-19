package com.carton.filesync.net;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import com.carton.filesync.common.util.SerializeUnit;
import com.carton.filesync.core.Core;
import com.carton.filesync.service.FileSynchronizer;

public class ControlSocket extends Thread {
	Thread reviceThread=new Thread() {
		public void run() {
			DataOutputStream makeUp=new DataOutputStream(pos);
			
			while(alive) {
				int commend;
				try {
					commend = cis.readInt();
					
					TCPState state=TCPState.getTCPStateByInt(commend);
					if(state==TCPState.CheckAliveRequest) {
						cos.writeInt(TCPState.CheckAliveReply.getType());
						cos.flush();
					}
					else if(runningFrame&&frame!=null){
						if(commend/100==1)
							cos.writeInt(TCPState.CommunicationError.getType());
						else{
							makeUp.writeInt(commend);
							makeUp.flush();
						}
						
					}else {
						TCPFrame newFrame=TCPFrame.getTCPFrame(state);
						frame=newFrame;
						runningFrame=true;
						reviceMode=true;
						cos.writeInt(state.getType()+1);
						asyncLock.notifyAll();
					}
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					continue;
				} catch (IOException e) {
				}
				catch(IllegalMonitorStateException e) {
					continue;
				}
				
			}
		}
	};
	Socket controlPipe;
	Socket dataPipe;
	PipedInputStream pis;
	PipedOutputStream pos;
	boolean dataPipeAvliable;
	DataOutputStream cos;
	DataInputStream cis;
	OutputStream dos;
	InputStream dis;
	private final static int TIMEOUT=2000;
	private final int RETRY=3;
	private final int BUFFSIZE=2048;
	private ArrayList<TCPFrame> frameList=new ArrayList<TCPFrame>();
	private TCPFrame frame;
	private ReentrantLock asyncLock=new ReentrantLock();
	boolean alive;
	boolean runningFrame;
	boolean reviceMode;
	ControlSocket(Socket controlPipe,Socket dataPipe) throws IOException {
		this.controlPipe=controlPipe;
		this.dataPipe=dataPipe;
		controlPipe.setSoTimeout(TIMEOUT);
		dataPipe.setSoTimeout(TIMEOUT);
		cos=new DataOutputStream(controlPipe.getOutputStream());
		cis=new DataInputStream(controlPipe.getInputStream());
		dos=dataPipe.getOutputStream();
		dis=dataPipe.getInputStream();
		dataPipeAvliable=true;
		pis=new PipedInputStream();
		pos=new PipedOutputStream();
		pis.connect(pos);
	}
	ControlSocket(Socket controlPipe) throws IOException {
		this.controlPipe=controlPipe;
		controlPipe.setSoTimeout(TIMEOUT);
		cos=new DataOutputStream(controlPipe.getOutputStream());
		cis=new DataInputStream(controlPipe.getInputStream());
		dataPipeAvliable=false;
		pis=new PipedInputStream();
		pos=new PipedOutputStream();
		pis.connect(pos);
	}
	public void addTCPFrame(TCPFrame frame) {
		if(this.frame==null){

			frame.isRevice=false;
			this.frame=frame;
		}
		else {
			synchronized(frameList){
				frameList.add(frame);
				
				try {
					asyncLock.notifyAll();
				}catch(IllegalMonitorStateException e) {
					
				}
				
			}
		}	
	}
	public void run() {
		alive=true;
		reviceThread.start();
		while(alive) {
			if(frame!=null) {
				runningFrame=true;
				synchronized(frame){
					if(reviceMode)
						frame.revice(cos, new DataInputStream(pis), dos, dis);
					else
						frame.send(cos, new DataInputStream(pis), dos, dis);
					reviceMode=false;
					frame=null;
				}
			}else if(frame==null&&!frameList.isEmpty()) {
				frame=frameList.remove(0);
			}else {
				try {
					asyncLock.wait(1000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println(e+"out");
				}catch(IllegalMonitorStateException e) {
					continue;
				}
			}
		}
	}
	public void setDataPipe(Socket dataPipe) throws IOException {
		synchronized(asyncLock){
			this.dataPipe=dataPipe;
			dataPipe.setSoTimeout(TIMEOUT);
			dos=dataPipe.getOutputStream();
			dis=dataPipe.getInputStream();
			dataPipeAvliable=true;
		}
	}
	public void close() {
		try {
			
			controlPipe.close();
			dataPipe.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}
	public static ControlSocket connectMachine(InetAddress ip,int port) throws IOException {
		Socket controlPipe=new Socket(ip,port);
		controlPipe.setSoTimeout(TIMEOUT);
		DataOutputStream cos=new DataOutputStream(controlPipe.getOutputStream());
		DataInputStream cis=new DataInputStream(controlPipe.getInputStream());
		cos.writeInt(TCPState.ControlPipeConnect.getType());
		cis.readInt();
		Socket dataPipe=new Socket(ip,port);
		dataPipe.setSoTimeout(TIMEOUT);
		cos=new DataOutputStream(dataPipe.getOutputStream());
		cis=new DataInputStream(dataPipe.getInputStream());
		cos.writeInt(TCPState.DataPipeConnect.getType());
		cis.readInt();
		ControlSocket controlSocket=new ControlSocket(controlPipe,dataPipe);
		
		return controlSocket;
	}
}
