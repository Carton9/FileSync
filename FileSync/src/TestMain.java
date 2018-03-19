import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.carton.filesync.common.util.ExtensionArrayList;
import com.carton.filesync.common.util.SerializeUnit;
import com.carton.filesync.core.Core;
import com.carton.filesync.file.*;
import com.carton.filesync.net.*;
import com.carton.filesync.service.FileListSyncUnit;

public class TestMain {
	public static String data="";
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		ControlSocketListener socketListener=new ControlSocketListener(3000);
		System.out.println("1");
		socketListener.start();
		System.out.println("2");
		InetAddress local=InetAddress.getLocalHost();
		ControlSocket controlSocket=ControlSocket.connectMachine(local, 3000);
		System.out.println("3");
		FileListSyncUnit unit=new FileListSyncUnit();
		TCPFrame f=TCPFrame.getTCPFrame(unit);
		controlSocket.start();
		controlSocket.addTCPFrame(f);
		System.out.println("4");
		for(int i=0;i<100;i++) {
			System.out.println(Core.frameList);
			//System.out.println(Core.unknowSocketList);
			Thread.sleep(2000);
		}
		System.out.println("ggg");
	}
}
/*
 * FileEventComplier complier=new FileEventComplier("D:\\C\\");
		FileWatcherConnecter connecter=new FileWatcherConnecter(11000,complier);
		Thread c=new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Timer t=new Timer();
				t.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						complier.complie();
						
					}
					
				}, 0, 100);
				t.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println(complier.getEventList());
					}
					
				}, 0, 20000);
			}
			
		});
		c.start();
		while(true) {
			connecter.run();
		}*/
/*
 * File c=new File("c://");
		File d=new File("d:/ccc/c.sss");
		String head=d.getAbsolutePath().substring(0,c.getAbsolutePath().length());
		ArrayList<File> fileList=new ArrayList<File>();
		fileList.add(d);
		System.out.println();
		System.out.println(d.getAbsolutePath().equals(d.getPath()));
		System.out.println(d.getCanonicalPath());
		System.out.println(head);
		d.getParentFile().mkdirs();
		d.createNewFile();*/
/*File file=new File("target.txt");
		
		FileInputStream fis=new FileInputStream(file);
		PipedInputStream pis=new PipedInputStream();
		PipedOutputStream pos=new PipedOutputStream();
		pis.connect(pos);
		Thread c;
		DataInputStream cis=new DataInputStream(pis);
		DataOutputStream cos=new DataOutputStream(pos);
		synchronized(pos) {
			c=new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						while(true)cos.writeInt(300);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(e);
					}
				}
				
			});
			c.start();
		}
		
		for(int i=0;i<300;i++) {
			System.out.println(cis.readInt());
			Thread.sleep(10);
			
		}
		pis.close();
		Thread.sleep(1000);
		c.stop();
		
	}*/


