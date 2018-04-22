import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.cartion.filesync.security.SHALog;
import com.carton.filesync.common.util.GeneralServiceExecutePool;
import com.carton.filesync.file.UniversalFileIO;
import com.carton.filesync.net.*;

public class TestMain {
	static ControlSocket Ssocket=null;
	static ControlSocket Csocket=null;
	static DuplexControlSocket socket=null;
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		/*Thread a=new Thread() {
			public void run() {
				try {
					Csocket=new SecurityControlSocket("127.0.0.1",3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Connector error "+e);
				}
			}
		};
		
		Thread c=new Thread() {
			public void run() {
				try {
					Ssocket=new SecurityControlSocket(3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Listener error "+e);
				}
			}
		};
		a.start();
		c.start();
		a.join();
		c.join();
		Thread p=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenPort();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread c2=new Thread() {
			public void run() {
				while(true) {
				}
					
			}
		};
		Thread p2=new Thread() {
			public void run() {
				while(true) {
					try {
						Csocket.listenControlPipe();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		p.start();
		p2.start();
		c2.start();*/
		/*socket=new DuplexControlSocket("127.0.0.1",3000);
		ArrayList<Integer> b=new ArrayList<Integer>();
		b.add(1);
		//ObejctFrame<ArrayList> of=new ObejctFrame<ArrayList>(b);
		File ff=new File("target.txt");
		System.out.println(ff.exists());
		for(int i=0;i<1;i++) {
			UniversalFileIO io=new UniversalFileIO(ff);
			//io.load();
			TCPFrame of=TCPFrame.createFrame(io);
			System.out.println(of.getClass());
			socket.submitFrame(of);
			Thread.sleep((new Random()).nextInt(1000)+10);
			System.out.println("send");
		}
		*/SHALog serverLog=new SHALog();
		SHALog clientLog=new SHALog(serverLog);
		//System.out.println(serverLog.veriftyID(clientLog.generateSign()));
		ServiceDiscover discover1=new ServiceDiscover(false,serverLog,new NetworkManager());
		ServiceDiscover discover2=new ServiceDiscover(true,clientLog,new NetworkManager());
		discover1.initialize();
		discover2.initialize();
		GeneralServiceExecutePool pool=new GeneralServiceExecutePool();
		System.out.println("lunch");
		pool.lunchUnit(discover2);
		System.out.println("lunch2");
		pool.lunchUnit(discover1);
		for(int i=0;i<3;i++){
			Thread.sleep(9000);
			System.out.println("# "+i);
		}
		pool.closePool();
	}
	
	 public static void writeInt(int v) throws IOException {
	        System.out.println((char)((v >>> 24) & 0xFF));
	        System.out.println((char)((v >>> 16) & 0xFF));
	        System.out.println((char)((v >>> 8) & 0xFF));
	        System.out.println((char)((v >>> 0) & 0xFF));
	    }
	/*
	 * public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }
	 * Thread a=new Thread() {
			public void run() {
				try {
					Csocket=new ControlSocket("127.0.0.1",3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Connector error "+e);
				}
			}
		};
		
		Thread c=new Thread() {
			public void run() {
				try {
					Ssocket=new ControlSocket(3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Listener error "+e);
				}
			}
		};
		a.start();
		c.start();
		a.join();
		c.join();
		Thread p=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenPort();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread c2=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenControlPipe();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
		};
		p.start();
		c2.start();
		System.out.print("get");
		ObejctFrame<String> of=new ObejctFrame<String>("ccc");
		Csocket.submitFrame(of);
		System.out.print("get2");
	 */
}
